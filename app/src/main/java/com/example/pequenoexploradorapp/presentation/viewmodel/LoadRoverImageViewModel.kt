package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.RoverImageInfo
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.repository.local.FavouriteImageRepositoryImpl
import com.example.pequenoexploradorapp.domain.repository.remote.RemoteRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LoadRoverImageViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val localRepositoryImpl: FavouriteImageRepositoryImpl,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoadRoverImageViewState>(LoadRoverImageViewState.Init)
    val uiState: StateFlow<LoadRoverImageViewState> = _uiState.asStateFlow()

    private var listOfImageFromApi = emptyList<RoverImageInfo>()
    private val _listOfImageFromApi = MutableStateFlow(listOfImageFromApi)

    private val _isLoading = MutableStateFlow((false))
    val isLoading = _isLoading.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )


    fun onSaveFavourite(
        imageFavouriteToSave: FavouriteImageToSave,
        listOfImage: List<RoverImageInfo>
    ) {
        viewModelScope.launch {
            if (checkIfFavouriteExist(imageFavouriteToSave)) (return@launch)
            _isLoading.value = true
            localRepositoryImpl.saveImage(imageFavouriteToSave)
            delay(800L)
            _isLoading.value = false
            _uiState.value = LoadRoverImageViewState.SuccessFavourite(listOfImage)
        }
    }

    private suspend fun checkIfFavouriteExist(listOfImagesFavourite: FavouriteImageToSave): Boolean {
        val favouriteImages = localRepositoryImpl.getFavouriteImage()
        return favouriteImages.any { it.link == listOfImagesFavourite.link }
    }

    private suspend fun updateFavouriteStatus(listOfImagesFromApi: List<RoverImageInfo>): List<RoverImageInfo> {
        val favouriteImages = localRepositoryImpl.getFavouriteImage()
        return listOfImagesFromApi.map { image ->
            val isFavourite = favouriteImages.any { it.link == image.imageUrl }
            image.copy(isFavourite = isFavourite)
        }
    }

    fun onRequestRoverImages(date: String, nameRover: String) {
        _uiState.value = LoadRoverImageViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            if (nameRover == BuildConfig.SPIRIT) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverSpiritImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> {
                        responseApi.data.photos.let { listOfImagesFromApi ->
                            _listOfImageFromApi.value = updateFavouriteStatus(listOfImagesFromApi)
                            _uiState.value = LoadRoverImageViewState.Success(
                                updateFavouriteStatus(listOfImagesFromApi)
                            )
                        }
                    }
                }
            }
            if (nameRover == BuildConfig.OPPORTUNITY) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverOpportunityImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> {
                        responseApi.data.photos.let { listOfImagesFromApi ->
                            _listOfImageFromApi.value = updateFavouriteStatus(listOfImagesFromApi)
                            _uiState.value = LoadRoverImageViewState.Success(
                                updateFavouriteStatus(listOfImagesFromApi)
                            )
                        }
                    }
                }
            }
            if (nameRover == BuildConfig.PERSEVERANCE) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverPerseveranceImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> {
                        responseApi.data.photos.let { listOfImagesFromApi ->
                            _listOfImageFromApi.value = updateFavouriteStatus(listOfImagesFromApi)
                            _uiState.value = LoadRoverImageViewState.Success(
                                updateFavouriteStatus(listOfImagesFromApi)
                            )
                        }
                    }
                }
            }
            if (nameRover == BuildConfig.CURIOSITY) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverCuriosityImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> {
                        responseApi.data.photos.let { listOfImagesFromApi ->
                            _listOfImageFromApi.value = updateFavouriteStatus(listOfImagesFromApi)
                            _uiState.value = LoadRoverImageViewState.Success(
                                updateFavouriteStatus(listOfImagesFromApi)
                            )
                        }
                    }
                }
            }
        }
    }
}


sealed interface LoadRoverImageViewState {
    data object Init : LoadRoverImageViewState
    data object Loading : LoadRoverImageViewState
    data class SuccessFavourite(val updateListOfImageFavourite: List<RoverImageInfo>) : LoadRoverImageViewState
    data class Success(val imagesFromApi: List<RoverImageInfo>) : LoadRoverImageViewState
    data class Error(val message: String) : LoadRoverImageViewState
}
