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
    private val localRepositoryImpl: FavouriteImageRepositoryImpl,
) : ViewModel() {

    private var listOfFavourites = emptyList<FavouriteImageToSave>()
    private val _listOfFavourites = MutableStateFlow(listOfFavourites)
    val listOfFavoriteImage: StateFlow<List<FavouriteImageToSave>> get() = _listOfFavourites

    private val _uiState = MutableStateFlow<LoadFavouriteImageViewState>(LoadFavouriteImageViewState.Init)
    val uiState: StateFlow<LoadFavouriteImageViewState> = _uiState.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun onGetFavouriteImageList() {
        viewModelScope.launch {
            delay(1000L)
            val response = localRepositoryImpl.getFavouriteImage()
            _listOfFavourites.value = response
            if(response.isNotEmpty()) _uiState.value = LoadFavouriteImageViewState.Success(response)
            else _uiState.value = LoadFavouriteImageViewState.Error(ConstantsApp.EMPTY_FAVOURITE_DB, true)
        }
    }

    fun onRemoveFavouriteImage(image: FavouriteImageToSave) {
        _uiState.value = LoadFavouriteImageViewState.RemoveFavourite(true)
        viewModelScope.launch {
            localRepositoryImpl.deleteImage(image)
            delay(600L)
            val response = localRepositoryImpl.getFavouriteImage()
            if(response == _listOfFavourites.value) {
                _uiState.value = LoadFavouriteImageViewState.RemoveFavourite(false)
                _uiState.value =LoadFavouriteImageViewState.Error(ConstantsApp.DEFAULT_ERROR_REMOVE_DB, true)
                (return@launch)
            }
            _listOfFavourites.value = response
            _uiState.value = LoadFavouriteImageViewState.RemoveFavourite(false)
            if(response.isNotEmpty()) _uiState.value = LoadFavouriteImageViewState.Success(response)
            else  _uiState.value = LoadFavouriteImageViewState.Error(ConstantsApp.EMPTY_FAVOURITE_DB, true)
        }
    }
}

sealed interface LoadFavouriteImageViewState {
    data object Init : LoadFavouriteImageViewState
    data class RemoveFavourite(val isLoading: Boolean) : LoadFavouriteImageViewState
    data class Success(val images: List<FavouriteImageToSave>) : LoadFavouriteImageViewState
    data class Error(val message: String, val isActivated: Boolean) : LoadFavouriteImageViewState
}
