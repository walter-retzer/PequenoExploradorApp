package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.NasaImageResponse
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

class LoadNasaImageViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoadNasaImageViewState>(LoadNasaImageViewState.Init)
    val uiState: StateFlow<LoadNasaImageViewState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow((false))
    val isLoading = _isLoading.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun onNasaImageSearch(imageSearch: String?) {
        _uiState.value = LoadNasaImageViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getNasaImage(imageSearch)) {
                is ApiResponse.Failure -> _uiState.value =
                    LoadNasaImageViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    LoadNasaImageViewState.Success(responseApi.data)

            }
        }
    }

    fun loadNextItems(
        page: Int,
        imageSearch: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getNasaImage(imageSearch, page)) {
                is ApiResponse.Failure -> _uiState.value =
                    LoadNasaImageViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    LoadNasaImageViewState.Success(responseApi.data)
            }
            _isLoading.value = false
        }
    }
}


sealed interface LoadNasaImageViewState {
    data object Loading : LoadNasaImageViewState
    data object Init : LoadNasaImageViewState
    data class Success(val images: NasaImageResponse) : LoadNasaImageViewState
    data class Error(val message: String) : LoadNasaImageViewState
}
