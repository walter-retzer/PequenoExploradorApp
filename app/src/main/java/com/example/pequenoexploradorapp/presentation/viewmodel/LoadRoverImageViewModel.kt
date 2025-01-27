package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.data.RoverCamera
import com.example.pequenoexploradorapp.data.RoverImageInfo
import com.example.pequenoexploradorapp.data.RoverImageResponse
import com.example.pequenoexploradorapp.data.RoverInfo
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

    val responseMock = RoverImageResponse(
        photos = listOf(
            RoverImageInfo(
                id = "1292742",
                sol = 1399,
                camera = RoverCamera(
                    id = "40",
                    name = "",
                    roverId = 8,
                    fullName = ""
                ),
                imageUrl = "https://mars.nasa.gov/mars2020-raw-images/pub/ods/surface/sol/01399/ids/edr/browse/zcam/ZR0_1399_0791132854_644EBY_N0650802ZCAM09454_0340LMJ01_1200.jpg",
                date = "2025-01-25",
                rover = RoverInfo(
                    id = "8",
                    name = "",
                    launchDate = "",
                    landingDate = "",
                    status = " "
                ),
                isFavourite = false
            ),
            RoverImageInfo(
                id = "1292742",
                sol = 1399,
                camera = RoverCamera(
                    id = "40",
                    name = "",
                    roverId = 8,
                    fullName = ""
                ),
                imageUrl = "https://mars.nasa.gov/mars2020-raw-images/pub/ods/surface/sol/01399/ids/edr/browse/zcam/ZR0_1399_0791132617_644EBY_N0650802ZCAM09454_0340LMJ01_1200.jpg",
                date = "2025-01-25",
                rover = RoverInfo(
                    id = "8",
                    name = "",
                    launchDate = "",
                    landingDate = "",
                    status = " "
                ),
                isFavourite = false
            ),
            RoverImageInfo(
                id = "1292742",
                sol = 1399,
                camera = RoverCamera(
                    id = "40",
                    name = "",
                    roverId = 8,
                    fullName = ""
                ),
                imageUrl = "https://mars.nasa.gov/mars2020-raw-images/pub/ods/surface/sol/01399/ids/edr/browse/zcam/ZR0_1399_0791132688_644EBY_N0650802ZCAM09454_0340LMJ01_1200.jpg",
                date = "2025-01-25",
                rover = RoverInfo(
                    id = "8",
                    name = "",
                    launchDate = "",
                    landingDate = "",
                    status = " "
                ),
                isFavourite = false
            ),

        )
    )

    private val _uiState = MutableStateFlow<LoadRoverImageViewState>(LoadRoverImageViewState.Init(responseMock))
    val uiState: StateFlow<LoadRoverImageViewState> = _uiState.asStateFlow()

    private var listOfImageFromApi = emptyList<RoverImageInfo>()
    private val _listOfImageFromApi = MutableStateFlow(listOfImageFromApi)
    val listOfImageToLoad: StateFlow<List<RoverImageInfo>> get() = _listOfImageFromApi

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
            if(checkIfFavouriteExist(imageFavouriteToSave)) (return@launch)
            _uiState.value = LoadRoverImageViewState.LoadingFavourite(true)
            localRepositoryImpl.saveImage(imageFavouriteToSave)
            delay(800L)
            _listOfImageFromApi.value = listOfImage
            _uiState.value = LoadRoverImageViewState.LoadingFavourite(false)
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
            val favouriteImages = localRepositoryImpl.getFavouriteImage()
            delay(3000L)
            if (nameRover == BuildConfig.SPIRIT) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverSpiritImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> _uiState.value =
                        LoadRoverImageViewState.Success(responseApi.data.photos)
                }
            }
            if (nameRover == BuildConfig.OPPORTUNITY) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverOpportunityImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> _uiState.value =
                        LoadRoverImageViewState.Success(responseApi.data.photos)
                }
            }
            if (nameRover == BuildConfig.PERSEVERANCE) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverPerseveranceImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)


                    is ApiResponse.Success -> {


                        responseApi.data.photos.let { imagesToLoad ->
                            _listOfImageFromApi.value = updateFavouriteStatus(imagesToLoad)
                            _uiState.value = LoadRoverImageViewState.Success(updateFavouriteStatus(imagesToLoad))
                            println(responseApi.data)
                        }






                    }
                }
            }
            if (nameRover == BuildConfig.CURIOSITY) {
                when (val responseApi =
                    remoteRepositoryImpl.getRoverCuriosityImages(date)) {
                    is ApiResponse.Failure -> _uiState.value =
                        LoadRoverImageViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> _uiState.value =
                        LoadRoverImageViewState.Success(responseApi.data.photos)
                }
            }
        }
    }
}


sealed interface LoadRoverImageViewState {
    data object Loading : LoadRoverImageViewState
    data class Init(val images: RoverImageResponse) : LoadRoverImageViewState
    data class LoadingFavourite(val isLoading: Boolean) : LoadRoverImageViewState
    data class SuccessFavourite(val updateListOfImageFavourite: List<RoverImageInfo>) : LoadRoverImageViewState
    data class Success(val images: List<RoverImageInfo>) : LoadRoverImageViewState
    data class Error(val message: String) : LoadRoverImageViewState
}
