package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.repository.local.FavouriteImageRepositoryImpl
import com.example.pequenoexploradorapp.domain.repository.remote.RemoteRepositoryImpl
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LoadNasaImageViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val dbImageNasaRepository: FavouriteImageRepositoryImpl,
) : ViewModel() {
    private var image = ""
    private var page = 2

    private var list = emptyList<NasaImageItems>()
    private val _listFlow = MutableStateFlow(list)
    val imageListFlow: StateFlow<List<NasaImageItems>> get() = _listFlow

    private val _uiState = MutableStateFlow<LoadNasaImageViewState>(LoadNasaImageViewState.Init)
    val uiState: StateFlow<LoadNasaImageViewState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow((false))
    val isLoading = _isLoading.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun onSaveFavourite(imageFavouriteToSave: FavouriteImageToSave, listOfImage: List<NasaImageItems>) {
        _uiState.value = LoadNasaImageViewState.LoadingFavourite(true)
        viewModelScope.launch {
            dbImageNasaRepository.save(imageFavouriteToSave)
            delay(800L)
            _listFlow.value = listOfImage
            _uiState.value = LoadNasaImageViewState.LoadingFavourite(false)
            _uiState.value = LoadNasaImageViewState.SuccessFavourite(listOfImage)
        }
    }

    private suspend fun updateFavouriteStatus(listOfImagesFromApi: List<NasaImageItems>): List<NasaImageItems> {
        val favouriteImages = dbImageNasaRepository.getFavouriteImage()
        return listOfImagesFromApi.map { image ->
            val isFavourite = favouriteImages.any { it.link == image.links.firstOrNull()?.href }
            image.copy(isFavourite = isFavourite)
        }
    }

    fun onNasaImageSearch(imageSearch: String?) {
        _uiState.value = LoadNasaImageViewState.Loading
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.PORTUGUESE)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val textTranslator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        textTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                println("Success Download Model Translation")
            }
            .addOnFailureListener { exception ->
                println("Error Download Model Translation: ${exception.printStackTrace()}")
            }
        textTranslator.translate(imageSearch.toString())
            .addOnSuccessListener { translatedText ->
                println("Success Text Translation: $translatedText")
                image = translatedText
                viewModelScope.launch {
                    when (val responseApi = remoteRepositoryImpl.getNasaImage(translatedText)) {
                        is ApiResponse.Failure -> _uiState.value =
                            LoadNasaImageViewState.Error(responseApi.messageError)

                        is ApiResponse.Success -> {
                            responseApi.data.collection.items?.let { imagesToLoad ->
                                _listFlow.value = updateFavouriteStatus(imagesToLoad)
                            }
                            _uiState.value = LoadNasaImageViewState.Success(responseApi.data)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error Text Translation: ${exception.printStackTrace()}")
            }
    }

    fun loadNextItems() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getNasaImage(image, page++)) {
                is ApiResponse.Failure -> _uiState.value =
                    LoadNasaImageViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> {
                    responseApi.data.collection.items?.let { imagesToLoad ->
                        _listFlow.value = imageListFlow.value + updateFavouriteStatus(imagesToLoad)
                    }
                    _uiState.value = LoadNasaImageViewState.Success(responseApi.data)
                }
            }
            _isLoading.value = false
        }
    }
}


sealed interface LoadNasaImageViewState {
    data object Init : LoadNasaImageViewState
    data object Loading : LoadNasaImageViewState
    data class LoadingFavourite(val isLoading: Boolean) : LoadNasaImageViewState
    data class Success(val images: NasaImageResponse) : LoadNasaImageViewState
    data class SuccessFavourite(val updateListOfImageFavourite: List<NasaImageItems>) : LoadNasaImageViewState
    data class Error(val message: String) : LoadNasaImageViewState
}
