package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.repository.RemoteRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SearchImageViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl
) : ViewModel() {

    private val _searchImageState = MutableStateFlow(SearchImageData())
    val searchImageState = _searchImageState.asStateFlow()

    private val _uiState = MutableStateFlow<SearchImageViewState>(SearchImageViewState.DrawScreen)
    val uiState: StateFlow<SearchImageViewState> = _uiState.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun onTextInputChange(newValue: String) {
        _searchImageState.update { it.copy(textInput = newValue) }
    }

    fun onNasaImageSearch(imageSearch: String) {
        _uiState.value = SearchImageViewState.Loading
        viewModelScope.launch {
            when (val responseApi = remoteRepositoryImpl.getNasaImage(imageSearch)) {
                is ApiResponse.Failure -> _uiState.value =
                    SearchImageViewState.Error(responseApi.messageError)

                is ApiResponse.Success -> _uiState.value =
                    SearchImageViewState.Success(responseApi.data)
            }
        }
    }
}

sealed interface SearchImageViewState {
    data object Loading : SearchImageViewState
    data object DrawScreen : SearchImageViewState
    data class Success(val collection: NasaImageResponse) : SearchImageViewState
    data class Error(val message: String) : SearchImageViewState
}

data class SearchImageData(
    val textInput: String = "",
    val textInputForReset: String = ""
)
