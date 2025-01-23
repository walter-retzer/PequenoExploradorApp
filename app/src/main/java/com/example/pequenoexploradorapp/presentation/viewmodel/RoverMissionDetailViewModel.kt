package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.repository.remote.RemoteRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoverMissionDetailViewModel(
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

    private val _uiState = MutableStateFlow<RoverMissionDetailViewState>(RoverMissionDetailViewState.Init)
    val uiState: StateFlow<RoverMissionDetailViewState> = _uiState.asStateFlow()


    fun onRoverMissionDetailRequest(nameRover: String?) {
        _uiState.value = RoverMissionDetailViewState.Loading
        viewModelScope.launch {
            delay(3000L)
            if(nameRover == BuildConfig.SPIRIT) {
                when (val responseApi = remoteRepositoryImpl.getRoverSpiritMission()) {
                    is ApiResponse.Failure -> _uiState.value =
                        RoverMissionDetailViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> _uiState.value =
                        RoverMissionDetailViewState.Success(responseApi.data)
                }
            }
            if(nameRover == BuildConfig.OPPORTUNITY) {
                when (val responseApi = remoteRepositoryImpl.getRoverOpportunityMission()) {
                    is ApiResponse.Failure -> _uiState.value =
                        RoverMissionDetailViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> _uiState.value =
                        RoverMissionDetailViewState.Success(responseApi.data)
                }
            }
            if(nameRover == BuildConfig.PERSEVERANCE) {
                when (val responseApi = remoteRepositoryImpl.getRoverPerseveranceMission()) {
                    is ApiResponse.Failure -> _uiState.value =
                        RoverMissionDetailViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> _uiState.value =
                        RoverMissionDetailViewState.Success(responseApi.data)
                }
            }
            if(nameRover == BuildConfig.CURIOSITY) {
                when (val responseApi = remoteRepositoryImpl.getRoverCuriosityMission()) {
                    is ApiResponse.Failure -> _uiState.value =
                        RoverMissionDetailViewState.Error(responseApi.messageError)

                    is ApiResponse.Success -> _uiState.value =
                        RoverMissionDetailViewState.Success(responseApi.data)
                }
            }
        }
    }
}

sealed interface RoverMissionDetailViewState {
    data object Loading : RoverMissionDetailViewState
    data object Init : RoverMissionDetailViewState
    data class Success(val mission: RoverMission) : RoverMissionDetailViewState
    data class Error(val message: String) : RoverMissionDetailViewState
}
