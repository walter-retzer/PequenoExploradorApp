package com.example.pequenoexploradorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pequenoexploradorapp.data.NewUserSignInContact
import com.example.pequenoexploradorapp.repository.RemoteRepositoryImpl
import com.example.pequenoexploradorapp.secure.SharedPrefApp
import com.example.pequenoexploradorapp.secure.UserPreferences
import com.example.pequenoexploradorapp.util.ConstantsApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SignInViewModel(private val remoteRepositoryImpl: RemoteRepositoryImpl) : ViewModel() {

    private val authService: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPref: SharedPrefApp = SharedPrefApp.instance

    private val _uiState = MutableStateFlow<SignInViewState>(SignInViewState.DrawScreen)
    val uiState: StateFlow<SignInViewState> = _uiState.asStateFlow()

    private val _newUserSignInState = MutableStateFlow(NewUserSignInContact())
    val newUserSignInState = _newUserSignInState.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateFlow()

    private val _nameError = MutableStateFlow(false)
    val nameError = _nameError.asStateFlow()

    private val _phoneNumberError = MutableStateFlow(false)
    val phoneNumberError = _phoneNumberError.asStateFlow()

    init {
        viewModelScope.launch {
            remoteRepositoryImpl.getInfoRoversMission()
        }
    }

    fun onSignInUser(name: String, email: String, password: String, phoneNumber: String) {
        _uiState.value = SignInViewState.Loading
        authService.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = SignInViewState.Success(ConstantsApp.SUCCESS_SIGN_IN)

                    if(authService.currentUser != null){
                        sharedPref.saveString(UserPreferences.NAME, name)
                        sharedPref.saveString( UserPreferences.EMAIL, email)
                        sharedPref.saveString(UserPreferences.PHONE, phoneNumber)
                        authService.currentUser?.uid?.let { sharedPref.saveString(UserPreferences.UID, it) }
                    }

                } else {
                    _uiState.value = SignInViewState.Error(ConstantsApp.ERROR_SIGN_IN)
                }
            }
    }

    fun onEmailChange(newValue: String) {
        _newUserSignInState.update { it.copy(email = newValue) }
        //reset error when the user types another character
        if (newValue.isNotBlank()) _emailError.value = false
    }

    fun onPasswordChange(newValue: String) {
        _newUserSignInState.update { it.copy(password = newValue) }
        //reset error when the user types another character
        if (newValue.isNotBlank()) _passwordError.value = false
    }

    fun onNameChange(newValue: String) {
        _newUserSignInState.update { it.copy(name = newValue) }
        //reset error when the user types another character
        if (newValue.isNotBlank()) _nameError.value = false
    }

    fun onPhoneNumberChange(newValue: String) {
        _newUserSignInState.update { it.copy(phoneNumber = newValue) }
        //reset error when the user types another character
        if (newValue.isNotBlank()) _phoneNumberError.value = false
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

    fun validatePhoneNumber(phoneNumber: String): String {
        if (phoneNumber.length != ConstantsApp.PHONE_MAX_NUMBER) {
            _phoneNumberError.value = true
        }
        return "Verifique o número digitado"
    }

    fun validateName(name: String): String {
        if (name.isEmpty()) {
            _nameError.value = true
        }
        return "Verifique o nome digitado"
    }
}


sealed interface SignInViewState {
    data object Loading : SignInViewState
    data object DrawScreen : SignInViewState
    data class Success(val message: String) : SignInViewState
    data class Error(val message: String) : SignInViewState
}
