package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.MessageFirebaseResponse
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.repository.remote.FirebaseDataBaseRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    fun questions() {
        viewModelScope.launch {
            _uiState.value = QuestionsViewState.Loading(true)
            val response = firebaseDataBase.getMessagesFlow()
            response.collect {
                _uiState.value = QuestionsViewState.Success(it)
            }
            response.catch { exception ->
                _uiState.value = QuestionsViewState.Error(exception.message.toString())
            }
        }
    }
}


sealed interface QuestionsViewState {
    data object Init : QuestionsViewState
    data class Loading(val isLoading: Boolean) : QuestionsViewState
    data class Success(val message: MessageFirebaseResponse) : QuestionsViewState
    data class Error(val message: String) : QuestionsViewState
}
