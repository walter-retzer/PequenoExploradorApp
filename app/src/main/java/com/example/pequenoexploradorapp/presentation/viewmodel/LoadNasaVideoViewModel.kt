package com.example.pequenoexploradorapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException


class LoadNasaVideoViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val localRepositoryImpl: FavouriteImageRepositoryImpl,
    private val handle: SavedStateHandle
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
}


sealed interface LoadNasaVideoViewState {
    data object Init : LoadNasaVideoViewState
    data class Loading(
        val isLoading: Boolean,
        val listOfNasaImage: List<NasaImageItems>,
        val totalHits: Int
    ) : LoadNasaVideoViewState

    data class SuccessAddFavourite(
        val updateListOfImageFavourite: List<NasaImageItems>,
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
