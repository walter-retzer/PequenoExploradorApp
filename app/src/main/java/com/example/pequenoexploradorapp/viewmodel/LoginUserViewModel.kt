package com.example.pequenoexploradorapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pequenoexploradorapp.data.FirebaseUserData
import com.example.pequenoexploradorapp.data.GoogleSignInResult
import com.example.pequenoexploradorapp.data.GoogleSignInState
import com.example.pequenoexploradorapp.data.GoogleUserData
import com.example.pequenoexploradorapp.secure.SharedPrefApp
import com.example.pequenoexploradorapp.secure.UserPreferences
import com.example.pequenoexploradorapp.util.ConstantsApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginUserViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPref: SharedPrefApp = SharedPrefApp.instance

    private val _state = MutableStateFlow(GoogleSignInState())
    val stateSignInGoogle = _state.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateFlow()

    private val _userLoginState = MutableStateFlow(FirebaseUserData())
    val userLoginState = _userLoginState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUserViewState>(LoginUserViewState.DrawScreen)
    val uiState: StateFlow<LoginUserViewState> = _uiState.asStateFlow()

    fun saveUserGoogleData( userGoogleData: GoogleUserData?){
        userGoogleData?.username?.let { sharedPref.saveString(UserPreferences.NAME, it) }
        userGoogleData?.email?.let { sharedPref.saveString(UserPreferences.EMAIL, it) }
        userGoogleData?.userId?.let { sharedPref.saveString(UserPreferences.UID, it) }
    }

    fun onGoogleSignInResult(result: GoogleSignInResult) {
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
        _uiState.value = LoginUserViewState.Loading
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
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.value = LoginUserViewState.Success(ConstantsApp.SUCCESS_RESET_PASSWORD)
            } else {
                _uiState.value = LoginUserViewState.Error(ConstantsApp.ERROR_RESET_PASSWORD)
            }
        }
    }

    fun onEmailForResetPasswordChange(newValue: String) {
        _userLoginState.update { it.copy(emailForResetPassword = newValue) }
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
    data object Loading : LoginUserViewState
    data object DrawScreen : LoginUserViewState
    data class Success(val message: String) : LoginUserViewState
    data class SuccessResetPassword(val message: String) : LoginUserViewState
    data class Error(val message: String) : LoginUserViewState
}
