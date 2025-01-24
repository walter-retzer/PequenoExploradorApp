package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.repository.local.FavouriteImageRepositoryImpl
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LoadFavouriteImageViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val dbImageNasaRepository: FavouriteImageRepositoryImpl,
) : ViewModel() {

    private var listOfFavourites = emptyList<FavouriteImageToSave>()
    private val _listOfFavourites = MutableStateFlow(listOfFavourites)
    val listOfFavoriteImage: StateFlow<List<FavouriteImageToSave>> get() = _listOfFavourites

    private val _uiState = MutableStateFlow<LoadFavouriteImageViewState>(LoadFavouriteImageViewState.Init)
    val uiState: StateFlow<LoadFavouriteImageViewState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow((false))
    val isLoading = _isLoading.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun onGetFavouriteImageList() {
        _uiState.value = LoadFavouriteImageViewState.Loading
        viewModelScope.launch {
            delay(3000L)

            val response = dbImageNasaRepository.getFavouriteImage()
            _listOfFavourites.value = response

            if(response.isNotEmpty()) _uiState.value = LoadFavouriteImageViewState.Success(response)
            else _uiState.value = LoadFavouriteImageViewState.Error(ConstantsApp.EMPTY_FAVOURITE_DB)
        }
    }

    fun onRemoveFavouriteImageList(image: FavouriteImageToSave) {
        _uiState.value = LoadFavouriteImageViewState.LoadingRemoveFavourite(true)
        viewModelScope.launch {
            dbImageNasaRepository.deleteImage(image)
            delay(600L)
            val response = dbImageNasaRepository.getFavouriteImage()
            if(response == _listOfFavourites.value) {
                _uiState.value = LoadFavouriteImageViewState.LoadingRemoveFavourite(false)
                _uiState.value =LoadFavouriteImageViewState.Error(ConstantsApp.DEFAULT_ERROR_REMOVE_DB)
                (return@launch)
            }
            _listOfFavourites.value = response
            _uiState.value = LoadFavouriteImageViewState.LoadingRemoveFavourite(false)
            if(response.isNotEmpty()) _uiState.value = LoadFavouriteImageViewState.Success(response)
            else  _uiState.value = LoadFavouriteImageViewState.Error(ConstantsApp.EMPTY_FAVOURITE_DB)
        }
    }
}

sealed interface LoadFavouriteImageViewState {
    data object Init : LoadFavouriteImageViewState
    data object Loading : LoadFavouriteImageViewState
    data class LoadingRemoveFavourite(val isLoading: Boolean) : LoadFavouriteImageViewState
    data class Success(val images: List<FavouriteImageToSave>) : LoadFavouriteImageViewState
    data class Error(val message: String) : LoadFavouriteImageViewState
}
