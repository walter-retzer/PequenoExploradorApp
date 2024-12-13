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
) : ViewModel(){

    private val _uiState = MutableStateFlow<LoadNasaImageViewState>(LoadNasaImageViewState.DrawScreen)
    val uiState: StateFlow<LoadNasaImageViewState> = _uiState.asStateFlow()

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

}

sealed interface LoadNasaImageViewState {
    data object Loading : LoadNasaImageViewState
    data object DrawScreen : LoadNasaImageViewState
    data class Success(val collection: NasaImageResponse) : LoadNasaImageViewState
    data class Error(val message: String) : LoadNasaImageViewState
}
