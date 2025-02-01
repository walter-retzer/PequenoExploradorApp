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


class LoadNasaVideoViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val localRepositoryImpl: FavouriteImageRepositoryImpl
) : ViewModel() {

    private var video = ""
    private var page = 1
    private var totalHits = 0

    private var listOfVideosFromApi = emptyList<NasaImageItems>()
    private val _listOfVideosFromApi = MutableStateFlow(listOfVideosFromApi)

    private val _uiState = MutableStateFlow<LoadNasaVideoViewState>(LoadNasaVideoViewState.Init)
    val uiState: StateFlow<LoadNasaVideoViewState> = _uiState.asStateFlow()

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
            _uiState.value = LoadNasaVideoViewState.Loading(
                true,
                listOfImage,
                totalHits
            )
            localRepositoryImpl.saveImage(imageFavouriteToSave)
            delay(800L)
            _listOfVideosFromApi.value = listOfImage
            _uiState.value = LoadNasaVideoViewState.Loading(
                false,
                listOfImage,
                totalHits
            )
            _uiState.value = LoadNasaVideoViewState.SuccessAddFavourite(
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

    fun onNasaVideoSearch(imageSearch: String?) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.PORTUGUESE)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val textTranslator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()
        textTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                println("Success Download Model Translation")
                textTranslator.translate(imageSearch.toString())
                    .addOnSuccessListener { translatedText ->
                        println("Success Text Translation: $translatedText")
                        video = translatedText
                        viewModelScope.launch {
                            when (val responseApi =
                                remoteRepositoryImpl.getNasaVideos(translatedText)) {
                                is ApiResponse.Failure -> _uiState.value =
                                    LoadNasaVideoViewState.Error(responseApi.messageError, true)

                                is ApiResponse.Success -> {
                                    totalHits = responseApi.data.collection.metadata?.totalHits ?: 0
                                    val items = responseApi.data.collection.items ?: emptyList()
                                    items.let { videosToLoad ->
                                        _listOfVideosFromApi.value = updateFavouriteStatus(videosToLoad)
                                    }

                                    _uiState.value = LoadNasaVideoViewState.SuccessVideo(
                                        _listOfVideosFromApi.value,
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

    fun loadNextVideos() {
        viewModelScope.launch {
            _uiState.value = LoadNasaVideoViewState.Loading(
                true,
                _listOfVideosFromApi.value,
                totalHits
            )
            delay(3000L)
            page++
            when (val responseApi = remoteRepositoryImpl.getNasaImage(video, page)) {
                is ApiResponse.Failure -> _uiState.value =
                    LoadNasaVideoViewState.Error(responseApi.messageError, true)

                is ApiResponse.Success -> {
                    responseApi.data.collection.items?.let { imagesToLoad ->
                        _listOfVideosFromApi.value += updateFavouriteStatus(imagesToLoad)
                    }
                    _uiState.value = LoadNasaVideoViewState.Loading(
                        false,
                        _listOfVideosFromApi.value,
                        totalHits
                    )
                    _uiState.value = LoadNasaVideoViewState.SuccessLoadMoreVideos(
                        _listOfVideosFromApi.value,
                        totalHits
                    )
                }
            }
        }
    }
}


sealed interface LoadNasaVideoViewState {
    data object Init : LoadNasaVideoViewState
    data class Loading(
        val isLoading: Boolean,
        val listOfNasaVideos: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaVideoViewState

    data class SuccessAddFavourite(
        val updateListOfVideoFavourite: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaVideoViewState

    data class SuccessLoadMoreVideos(
        val updateListOfVideos: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaVideoViewState

    data class SuccessVideo(
        val video: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaVideoViewState

    data class Error(
        val message: String,
        val isActivated: Boolean
    ) : LoadNasaVideoViewState
}
