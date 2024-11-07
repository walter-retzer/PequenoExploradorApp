package com.example.pequenoexploradorapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pequenoexploradorapp.util.ConstantsApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SignInViewModel : ViewModel() {

    private val authService: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<SignInViewState>(SignInViewState.Dashboard)
    val uiState: StateFlow<SignInViewState> = _uiState.asStateFlow()

    private val _newUserSignInState = MutableStateFlow(NewUserContact())
    val newUserSignInState = _newUserSignInState.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateFlow()

    private val _nameError = MutableStateFlow(false)
    val nameError = _nameError.asStateFlow()

    private val _phoneNumberError = MutableStateFlow(false)
    val phoneNumberError = _phoneNumberError.asStateFlow()


    fun onSignInUser(email: String, password: String) {
        _uiState.value = SignInViewState.Loading
        authService.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = SignInViewState.Success(ConstantsApp.SUCCESS_SIGN_IN)
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

data class NewUserContact(
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
)

sealed interface SignInViewState {
    data object Loading : SignInViewState
    data object Dashboard : SignInViewState
    data class Success(val message: String) : SignInViewState
    data class Error(val message: String) : SignInViewState
}
