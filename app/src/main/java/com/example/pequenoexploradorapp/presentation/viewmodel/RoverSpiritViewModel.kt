package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.RoverMissionSpirit
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

class RoverSpiritViewModel(
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

    private val _uiState = MutableStateFlow<RoverSpiritViewState>(RoverSpiritViewState.Init)
    val uiState: StateFlow<RoverSpiritViewState> = _uiState.asStateFlow()


    fun onRoverSpiritMissionRequest() {
        _uiState.value = RoverSpiritViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            when (val responseApi = remoteRepositoryImpl.getRoverSpiritMission()) {
                is ApiResponse.Failure -> _uiState.value =
                    RoverSpiritViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    RoverSpiritViewState.Success(responseApi.data)
            }
        }
    }
}

sealed interface RoverSpiritViewState {
    data object Loading : RoverSpiritViewState
    data object Init : RoverSpiritViewState
    data class Success(val mission: RoverMissionSpirit) : RoverSpiritViewState
    data class Error(val message: String) : RoverSpiritViewState
}