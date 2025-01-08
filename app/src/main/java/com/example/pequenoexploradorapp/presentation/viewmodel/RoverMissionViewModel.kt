package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.PictureOfTheDay
import com.example.pequenoexploradorapp.data.RoverMission
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

class RoverMissionViewModel(
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

    private val _uiState = MutableStateFlow<RoverMissionViewState>(RoverMissionViewState.Init)
    val uiState: StateFlow<RoverMissionViewState> = _uiState.asStateFlow()


    fun onInfoRoversMissionRequest() {
        _uiState.value = RoverMissionViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getInfoRoversMission()) {
                is ApiResponse.Failure -> _uiState.value =
                    RoverMissionViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    RoverMissionViewState.Success(responseApi.data)

            }
        }
    }
}

sealed interface RoverMissionViewState {
    data object Loading : RoverMissionViewState
    data object Init : RoverMissionViewState
    data class Success(val mission: RoverMission) : RoverMissionViewState
    data class Error(val message: String) : RoverMissionViewState
}