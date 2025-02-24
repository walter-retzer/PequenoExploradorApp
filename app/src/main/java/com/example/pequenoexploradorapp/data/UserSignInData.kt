package com.example.pequenoexploradorapp.data

data class GoogleSignInResult(
    val data: GoogleUserData?,
    val errorMessage: String?
)

data class GoogleSignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

data class GoogleUserData(
    val userId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?
)

data class FirebaseUserData(
    val email: String = "",
    val password: String = "",
    val emailForResetPassword: String = ""
)

data class NewUserSignInContact(
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
)
