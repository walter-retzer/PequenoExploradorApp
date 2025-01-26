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

    private val _isLoading = MutableStateFlow((false))
    val isLoading = _isLoading.asStateFlow()

    fun onSaveFavourite(
        imageFavouriteToSave: FavouriteImageToSave,
        image: PictureOfTheDay
    ) {
        viewModelScope.launch {
            if (checkIfFavouriteExist(imageFavouriteToSave)) (return@launch)
            _isLoading.value = true
            localRepositoryImpl.saveImage(imageFavouriteToSave)
            delay(800L)
            _isLoading.value = false
            _uiState.value = PictureOfTheDayViewState.Success(image.copy(isFavourite = true))
        }
    }

    private suspend fun checkIfFavouriteExist(listOfImagesFavourite: FavouriteImageToSave): Boolean {
        val favouriteImages = localRepositoryImpl.getFavouriteImage()
        return favouriteImages.any { it.link == listOfImagesFavourite.link }
    }

    fun onPictureOfTheDayRequest() {
        _uiState.value = PictureOfTheDayViewState.Loading
        viewModelScope.launch {
            val favouriteImages = localRepositoryImpl.getFavouriteImage()
            delay(1000L)
            when (val responseApi = remoteRepositoryImpl.getPictureOfTheDay()) {
                is ApiResponse.Failure -> _uiState.value =
                    PictureOfTheDayViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> {
                    val options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.PORTUGUESE)
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
}

sealed interface PictureOfTheDayViewState {
    data object Init : PictureOfTheDayViewState
    data object Loading : PictureOfTheDayViewState
    data class Success(val image: PictureOfTheDay) : PictureOfTheDayViewState
    data class SuccessVideoUrl(val videoUrl: PictureOfTheDay) : PictureOfTheDayViewState
    data class Error(val message: String) : PictureOfTheDayViewState
}