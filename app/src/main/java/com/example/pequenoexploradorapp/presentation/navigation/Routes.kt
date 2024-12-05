package com.example.pequenoexploradorapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route(
    val route: String
) {
    @Serializable
    object SplashScreenRoute : Route(route = "SplashScreen")

    @Serializable
    object WelcomeScreenRoute : Route(route = "WelcomeScreen")

    @Serializable
    object HomeScreenRoute : Route(route = "HomeScreen")

    @Serializable
    object LoginScreenRoute : Route(route = "LoginScreen")

    @Serializable
    object SignInScreenRoute : Route(route = "SignInScreen")

    @Serializable
    object LoginGraphNav : Route(route = "LoginNavigation")

    @Serializable
    object HomeGraphNav : Route(route = "HomeNavigation")
}
