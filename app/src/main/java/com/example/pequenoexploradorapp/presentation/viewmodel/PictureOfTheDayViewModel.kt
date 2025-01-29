package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.PictureOfTheDay
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


class PictureOfTheDayViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val localRepositoryImpl: FavouriteImageRepositoryImpl
) : ViewModel() {
    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    private val _uiState = MutableStateFlow<PictureOfTheDayViewState>(PictureOfTheDayViewState.Init)
    val uiState: StateFlow<PictureOfTheDayViewState> = _uiState.asStateFlow()

    fun onSaveFavourite(
        imageFavouriteToSave: FavouriteImageToSave,
        image: PictureOfTheDay
    ) {
        viewModelScope.launch {
            if (checkIfFavouriteExist(imageFavouriteToSave)) (return@launch)
            _uiState.value = PictureOfTheDayViewState.SaveFavourite(image, true)
            localRepositoryImpl.saveImage(imageFavouriteToSave)
            delay(800L)
            _uiState.value = PictureOfTheDayViewState.SaveFavourite(image, true)
            _uiState.value = PictureOfTheDayViewState.Success(image.copy(isFavourite = true))
        }
    }

    private suspend fun checkIfFavouriteExist(listOfImagesFavourite: FavouriteImageToSave): Boolean {
        val favouriteImages = localRepositoryImpl.getFavouriteImage()
        return favouriteImages.any { it.link == listOfImagesFavourite.link }
    }

    fun onPictureOfTheDayRequest() {
        var favouriteImages = emptyList<FavouriteImageToSave>()
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.PORTUGUESE)
            .build()
        val textTranslator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()
        viewModelScope.launch {
            favouriteImages = localRepositoryImpl.getFavouriteImage()
        }
        textTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                println("Success Download Model Translation")
                viewModelScope.launch {
                    when (val responseApi = remoteRepositoryImpl.getPictureOfTheDay()) {
                        is ApiResponse.Failure -> _uiState.value =
                            PictureOfTheDayViewState.Error(responseApi.messageError, true)

                        is ApiResponse.Success -> {
                            responseApi.data.explanation?.let {
                                textTranslator.translate(it)
                                    .addOnSuccessListener { translatedText ->
                                        println("Success Text Translation: $translatedText")
                                        if (responseApi.data.mediaType == "video") _uiState.value =
                                            PictureOfTheDayViewState.SuccessVideoUrl(
                                                videoUrl = responseApi.data.copy(
                                                    explanation = translatedText
                                                )
                                            )
                                        else {
                                            val isFavourite = favouriteImages.any { favourite ->
                                                favourite.link == responseApi.data.url
                                            }
                                            _uiState.value = PictureOfTheDayViewState.Success(
                                                image = responseApi.data.copy(
                                                    explanation = translatedText,
                                                    isFavourite = isFavourite
                                                )
                                            )
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        println("Error Text Translation: ${exception.printStackTrace()}")
                                    }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error Download Model Translation: ${exception.printStackTrace()}")
            }

    }
}


sealed interface PictureOfTheDayViewState {
    data object Init : PictureOfTheDayViewState
    data class SaveFavourite(val image: PictureOfTheDay, val isLoading: Boolean) : PictureOfTheDayViewState
    data class Success(val image: PictureOfTheDay) : PictureOfTheDayViewState
    data class SuccessVideoUrl(val videoUrl: PictureOfTheDay) : PictureOfTheDayViewState
    data class Error(val message: String, val isActivated: Boolean) : PictureOfTheDayViewState
}