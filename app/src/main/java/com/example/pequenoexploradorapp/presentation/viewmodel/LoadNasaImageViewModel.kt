package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.NasaImageItems
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
    private val localRepositoryImpl: FavouriteImageRepositoryImpl,
) : ViewModel() {
    private var image = ""
    private var page = 1
    private var totalHits = 0

    private var listOfImageFromApi = emptyList<NasaImageItems>()
    private val _listOfImageFromApi = MutableStateFlow(listOfImageFromApi)

    private val _uiState = MutableStateFlow<LoadNasaImageViewState>(LoadNasaImageViewState.Init)
    val uiState: StateFlow<LoadNasaImageViewState> = _uiState.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun onSaveFavourite(
        imageFavouriteToSave: FavouriteImageToSave,
        listOfImage: List<NasaImageItems>
    ) {
        println(listOfImage)
        viewModelScope.launch {
            if (checkIfFavouriteExist(imageFavouriteToSave)) (return@launch)
            _uiState.value = LoadNasaImageViewState.Loading(
                true,
                listOfImage,
                totalHits
            )
            localRepositoryImpl.saveImage(imageFavouriteToSave)
            delay(800L)
            _listOfImageFromApi.value = listOfImage
            _uiState.value = LoadNasaImageViewState.Loading(
                false,
                listOfImage,
                totalHits
            )
            _uiState.value = LoadNasaImageViewState.SuccessAddFavourite(
                listOfImage,
                totalHits
            )
        }
    }

    private suspend fun checkIfFavouriteExist(listOfImagesFavourite: FavouriteImageToSave): Boolean {
        val favouriteImages = localRepositoryImpl.getFavouriteImage()
        return favouriteImages.any { it.link == listOfImagesFavourite.link }
    }

    private suspend fun updateFavouriteStatus(listOfImagesFromApi: List<NasaImageItems>): List<NasaImageItems> {
        val favouriteImages = localRepositoryImpl.getFavouriteImage()
        return listOfImagesFromApi.map { image ->
            val isFavourite = favouriteImages.any { it.link == image.links.firstOrNull()?.href }
            image.copy(isFavourite = isFavourite)
        }
    }

    fun onNasaImageSearch(imageSearch: String?) {
        _uiState.value = LoadNasaImageViewState.FirstLoading
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
                textTranslator.translate(imageSearch.toString())
                    .addOnSuccessListener { translatedText ->
                        println("Success Text Translation: $translatedText")
                        image = translatedText
                        viewModelScope.launch {
                            when (val responseApi =
                                remoteRepositoryImpl.getNasaImage(translatedText)) {
                                is ApiResponse.Failure -> _uiState.value =
                                    LoadNasaImageViewState.Error(responseApi.messageError)

                                is ApiResponse.Success -> {
                                    responseApi.data.collection.items?.let { imagesToLoad ->
                                        _listOfImageFromApi.value =
                                            updateFavouriteStatus(imagesToLoad)
                                    }
                                    totalHits = responseApi.data.collection.metadata?.totalHits ?: 0
                                    _uiState.value = LoadNasaImageViewState.Success(
                                        _listOfImageFromApi.value,
                                        totalHits
                                    )
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Error Text Translation: ${exception.printStackTrace()}")
                    }
            }
            .addOnFailureListener { exception ->
                println("Error Download Model Translation: ${exception.printStackTrace()}")
            }
    }

    fun loadNextImage() {
        viewModelScope.launch {
            _uiState.value = LoadNasaImageViewState.Loading(
                true,
                _listOfImageFromApi.value,
                totalHits
            )
            delay(3000L)
            page++
            when (val responseApi = remoteRepositoryImpl.getNasaImage(image, page)) {
                is ApiResponse.Failure -> _uiState.value =
                    LoadNasaImageViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> {
                    responseApi.data.collection.items?.let { imagesToLoad ->
                        _listOfImageFromApi.value += updateFavouriteStatus(imagesToLoad)
                    }
                    _uiState.value = LoadNasaImageViewState.Loading(
                        false,
                        _listOfImageFromApi.value,
                        totalHits
                    )
                    _uiState.value = LoadNasaImageViewState.Success(
                        _listOfImageFromApi.value,
                        totalHits
                    )
                }
            }
        }
    }
}


sealed interface LoadNasaImageViewState {
    data object Init : LoadNasaImageViewState
    data object FirstLoading : LoadNasaImageViewState
    data class Loading(
        val isLoading: Boolean,
        val listOfNasaImage: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaImageViewState

    data class SuccessAddFavourite(
        val updateListOfImageFavourite: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaImageViewState

    data class Success(
        val images: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaImageViewState

    data class Error(val message: String) : LoadNasaImageViewState
}
