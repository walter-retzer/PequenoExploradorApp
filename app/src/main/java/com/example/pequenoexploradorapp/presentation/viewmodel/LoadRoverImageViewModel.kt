package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.RoverImageResponse
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.repository.RemoteRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoadRoverImageViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoadRoverImageViewState>(LoadRoverImageViewState.Init)
    val uiState: StateFlow<LoadRoverImageViewState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow((false))
    val isLoading = _isLoading.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun onRoverSpiritImages(date: String? = "") {
        _uiState.value = LoadRoverImageViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getRoverSpiritImages()) {
                is ApiResponse.Failure -> _uiState.value =
                    LoadRoverImageViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    LoadRoverImageViewState.Success(responseApi.data)

            }
        }
    }

    fun loadNextItems(
        page: Int,
        date: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getRoverSpiritImages()) {
                is ApiResponse.Failure -> _uiState.value =
                    LoadRoverImageViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    LoadRoverImageViewState.Success(responseApi.data)
            }
            _isLoading.value = false
        }
    }
}


sealed interface LoadRoverImageViewState {
    data object Loading : LoadRoverImageViewState
    data object Init : LoadRoverImageViewState
    data class Success(val images: RoverImageResponse) : LoadRoverImageViewState
    data class Error(val message: String) : LoadRoverImageViewState
}
