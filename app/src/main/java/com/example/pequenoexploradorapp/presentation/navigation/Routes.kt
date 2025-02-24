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
    object PictureOfTheDayScreenRoute : Route(route = "PictureOfTheDayScreen")

    @Serializable
    object RoverMissionScreenRoute : Route(route = "RoverMissionScreen")

    @Serializable
    object RoverMissionDetailScreenRoute : Route(route = "RoverMissionDetailScreen")

    @Serializable
    object RoverSearchImageScreenRoute : Route(route = "RoverSearchImageScreen")

    @Serializable
    object LoadRoverImageScreenRoute : Route(route = "LoadRoverImageScreen")

    @Serializable
    object LoadFavouriteImageScreenRoute : Route(route = "LoadFavouriteImageScreen")

    @Serializable
    object ShareFavouriteImageScreenRoute : Route(route = "ShareFavouriteImageScreen")

    @Serializable
    object LoadNasaVideoScreenRoute : Route(route = "LoadNasaVideoScreen")

    @Serializable
    object NasaVideoDetailScreenRoute : Route(route = "NasaVideoDetailScreen")

    @Serializable
    object SearchNasaVideosScreenRoute : Route(route = "SearchNasaVideosScreen")

    @Serializable
    object PlanetsScreenRoute : Route(route = "PlanetsScreen")

    @Serializable
    object GallerySearchScreenRoute : Route(route = "GallerySearchScreen")

    @Serializable
    object ProfileScreenRoute : Route(route = "ProfileScreen")

    @Serializable
    object AvatarSelectionScreenRoute : Route(route = "AvatarSelectionScreen")

    @Serializable
    object QuestionsScreenRoute : Route(route = "QuestionsScreen")

    // Routes from Graph Navigation:
    @Serializable
    object LoginGraphNav : Route(route = "LoginNavigation")

    @Serializable
    object HomeGraphNav : Route(route = "HomeNavigation")
}
