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
    object LoginScreenRoute : Route(route = "LoginScreen")

    @Serializable
    object SignInScreenRoute : Route(route = "SignInScreen")

    @Serializable
    object HomeScreenRoute : Route(route = "HomeScreen")

    @Serializable
    object SearchImageScreenRoute : Route(route = "SearchImageScreen")

    @Serializable
    object LoadNasaImageScreenRoute : Route(route = "LoadNasaImageScreen")

    @Serializable
    object FavouriteImageScreenRoute : Route(route = "FavouriteNasaImageScreen")

    @Serializable
    object PictureOfTheDayScreenRoute : Route(route = "PictureOfTheDayScreen")

    @Serializable
    object RoverMissionScreenRoute : Route(route = "RoverMissionScreen")

    @Serializable
    object RoverMissionDetailScreenRoute : Route(route = "RoverMissionDetailScreen")

    @Serializable
    object RoverSearchImageScreenRoute : Route(route = "RoverSearchImageScreen")

    @Serializable
    object LoadRoverImageScreenRoute : Route(route = "LoadRoverImageScreen")

    // Routes from Graph Navigation:
    @Serializable
    object LoginGraphNav : Route(route = "LoginNavigation")

    @Serializable
    object HomeGraphNav : Route(route = "HomeNavigation")
}
