package com.example.pequenoexploradorapp.data

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)
