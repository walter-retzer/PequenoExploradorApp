package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


class SearchNasaViewModel(
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _searchImageState = MutableStateFlow(SearchVideosData())
    val searchImageState = _searchImageState.asStateFlow()

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

}

data class SearchVideosData(
    val textInput: String = "",
    val textInputForReset: String = ""
)
