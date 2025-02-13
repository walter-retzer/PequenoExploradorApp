package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.domain.secure.SharedPrefApp
import com.example.pequenoexploradorapp.domain.secure.UserPreferences
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val sharedPref: SharedPrefApp
) : ViewModel() {
    private val authService: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<ProfileViewState>(ProfileViewState.Dashboard)
    val uiState: StateFlow<ProfileViewState> = _uiState.asStateFlow()

    fun onSignOut() {
        _uiState.value = ProfileViewState.Loading
        viewModelScope.launch {
            try {
                authService.signOut()
                sharedPref.deleteId(UserPreferences.UID)
                delay(3000L)
                _uiState.value = ProfileViewState.Success
            } catch (e: Exception) {
                println(e)
                _uiState.value = ProfileViewState.Error(ConstantsApp.ERROR_SIGN_OUT)
            }
        }
    }

    fun onDeleteAccount() {
        _uiState.value = ProfileViewState.Loading
        viewModelScope.launch {
            try {
                authService.currentUser?.delete()
                authService.signOut()
                sharedPref.deleteAll()
                _uiState.value = ProfileViewState.SuccessClearInfoUser(ConstantsApp.SUCCESS_DELETE_ACCOUNT)
                delay(3000L)
                _uiState.value = ProfileViewState.Success
            } catch (e: Exception) {
                println(e)
                _uiState.value = ProfileViewState.Error(ConstantsApp.ERROR_DELETE_ACCOUNT)
            }
        }
    }
}

sealed interface ProfileViewState {
    data object Loading : ProfileViewState
    data object Dashboard : ProfileViewState
    data object Success : ProfileViewState
    data class SuccessClearInfoUser(val message: String) : ProfileViewState
    data class Error(val message: String) : ProfileViewState
}