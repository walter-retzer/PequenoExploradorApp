package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.FirebaseDataBaseResponse
import com.example.pequenoexploradorapp.data.ResponseFirebase
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.repository.remote.FirebaseDataBaseRepositoryImpl
import com.example.pequenoexploradorapp.domain.util.ConstantsApp.Companion.ERROR_SERVER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuestionsViewModel(
    private val firebaseDataBase: FirebaseDataBaseRepositoryImpl,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionsViewState>(QuestionsViewState.Init)
    val uiState: StateFlow<QuestionsViewState> = _uiState.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun getQuestions() {
        viewModelScope.launch {
            _uiState.value = QuestionsViewState.Loading(true)
            val response = firebaseDataBase.getMessagesFlow()
            response.collect { responseFirebase ->
                when(responseFirebase){
                    is ResponseFirebase.Failure -> {
                        _uiState.value = QuestionsViewState.Error(ERROR_SERVER)
                    }
                    is ResponseFirebase.Success -> {
                        _uiState.value = QuestionsViewState.Success(responseFirebase.data)
                    }
                }
            }
        }
    }
}


sealed interface QuestionsViewState {
    data object Init : QuestionsViewState
    data class Loading(val isLoading: Boolean) : QuestionsViewState
    data class Success(val response: FirebaseDataBaseResponse) : QuestionsViewState
    data class Error(val message: String) : QuestionsViewState
}
