package com.example.pequenoexploradorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.FirebaseUserData
import com.example.pequenoexploradorapp.data.GoogleSignInResult
import com.example.pequenoexploradorapp.data.GoogleSignInState
import com.example.pequenoexploradorapp.data.GoogleUserData
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.secure.SharedPrefApp
import com.example.pequenoexploradorapp.domain.secure.UserPreferences
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class LoginUserViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val sharedPref: SharedPrefApp
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(GoogleSignInState())
    val stateSignInGoogle = _state.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError = _emailError.asStateFlow()

    private val _emailResetError = MutableStateFlow(false)
    val emailResetError = _emailResetError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateFlow()

    private val _userLoginState = MutableStateFlow(FirebaseUserData())
    val userLoginState = _userLoginState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUserViewState>(LoginUserViewState.Init)
    val uiState: StateFlow<LoginUserViewState> = _uiState.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun saveUserGoogleData(userGoogleData: GoogleUserData?) {
        userGoogleData?.username?.let { sharedPref.saveString(UserPreferences.NAME, it) }
        userGoogleData?.email?.let { sharedPref.saveString(UserPreferences.EMAIL, it) }
        userGoogleData?.userId?.let { sharedPref.saveString(UserPreferences.UID, it) }
    }

    fun onGoogleSignInResult(result: GoogleSignInResult) {
        _uiState.value = LoginUserViewState.Loading(true)
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { GoogleSignInState() }
    }

    fun onFirebaseAuthSignIn(email: String, password: String) {
        _uiState.value = LoginUserViewState.Loading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = LoginUserViewState.Success(ConstantsApp.SUCCESS_SIGN_IN)
                } else {
                    _uiState.value = LoginUserViewState.Error(ConstantsApp.ERROR_SIGN_IN)
                }
            }
    }

    fun onFirebaseAuthResetPassword(email: String) {
        _uiState.value = LoginUserViewState.Loading(true)
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.value = LoginUserViewState.SuccessResetPassword(ConstantsApp.SUCCESS_RESET_PASSWORD)
            } else {
                _uiState.value = LoginUserViewState.Error(ConstantsApp.ERROR_RESET_PASSWORD)
            }
        }
    }

    fun onEmailForResetPasswordChange(newValue: String) {
        _userLoginState.update { it.copy(emailForResetPassword = newValue) }
        if (newValue.isNotBlank()) _emailResetError.value = false
    }

    fun onEmailChange(newValue: String) {
        _userLoginState.update { it.copy(email = newValue) }
        //reset error when the user types another character
        if (newValue.isNotBlank()) _emailError.value = false
    }

    fun onPasswordChange(newValue: String) {
        _userLoginState.update { it.copy(password = newValue) }
        //reset error when the user types another character
        if (newValue.isNotBlank()) _passwordError.value = false
    }

    fun validateEmail(email: String): String {
        val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        if (!emailRegex.matches(email)) {
            _emailError.value = true
        }
        return "Verifique o email digitado"
    }


    fun validatePassword(password: String): String {
        if (password.length != ConstantsApp.PASSWORD_MAX_NUMBER) {
            _passwordError.value = true
        }
        return "A senha deve conter 6 dígitos"
    }
}


sealed interface LoginUserViewState {
    data object Init : LoginUserViewState
    data class Loading(val isLoading: Boolean) : LoginUserViewState
    data class Success(val message: String) : LoginUserViewState
    data class SuccessResetPassword(val message: String) : LoginUserViewState
    data class Error(val message: String) : LoginUserViewState
}
