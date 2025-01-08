package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.PictureOfTheDay
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

class PictureOfTheDayViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl
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


    fun onPictureOfTheDayRequest() {
        _uiState.value = PictureOfTheDayViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getPictureOfTheDay()) {
                is ApiResponse.Failure -> _uiState.value =
                    PictureOfTheDayViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    PictureOfTheDayViewState.Success(responseApi.data)

            }
        }
    }
}

sealed interface PictureOfTheDayViewState {
    data object Loading : PictureOfTheDayViewState
    data object Init : PictureOfTheDayViewState
    data class Success(val image: PictureOfTheDay) : PictureOfTheDayViewState
    data class Error(val message: String) : PictureOfTheDayViewState
}