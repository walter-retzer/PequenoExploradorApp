package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.NasaImageData
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.repository.NasaImageRepository
import com.example.pequenoexploradorapp.domain.repository.RemoteRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LoadFavouriteImageViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val dbImageNasaRepository: NasaImageRepository,
) : ViewModel() {

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

    fun onLoadListFavouriteImage() {
        LoadFavouriteImageViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            val response = dbImageNasaRepository.getFavouriteImage()
            println(response)
            if(response.isNotEmpty()) LoadFavouriteImageViewState.Success(response)
            else  LoadFavouriteImageViewState.Error("Erro DB")
        }
    }
}

sealed interface LoadFavouriteImageViewState {
    data object Loading : LoadFavouriteImageViewState
    data object Init : LoadFavouriteImageViewState
    data class Success(val images: List<NasaImageData>) : LoadFavouriteImageViewState
    data class Error(val message: String) : LoadFavouriteImageViewState
}
