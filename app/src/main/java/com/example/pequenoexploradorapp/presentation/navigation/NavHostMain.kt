package com.example.pequenoexploradorapp.presentation.navigation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.pequenoexploradorapp.domain.util.GoogleAuthUiClient
import com.example.pequenoexploradorapp.domain.util.getLocalDateFormattedApi
import com.example.pequenoexploradorapp.presentation.components.AppBottomNavigationBar
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.DATE_FINAL_KEY
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.DATE_INITIAL_KEY
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.DATE_KEY
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.DATE_TO_SEARCH
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.ID_NAME_KEY
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.IMAGE_ROVER
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.IMAGE_SEARCH_KEY
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.IMAGE_TO_SHARE
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.TOOLBAR
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.VIDEO_SEARCH_KEY
import com.example.pequenoexploradorapp.presentation.navigation.ArgumentsKey.VIDEO_TO_LOAD
import com.example.pequenoexploradorapp.presentation.screen.GallerySearchScreen
import com.example.pequenoexploradorapp.presentation.screen.HomeMenuScreen
import com.example.pequenoexploradorapp.presentation.screen.LoadFavouriteImageScreen
import com.example.pequenoexploradorapp.presentation.screen.LoadNasaImageScreen
import com.example.pequenoexploradorapp.presentation.screen.LoadNasaVideoScreen
import com.example.pequenoexploradorapp.presentation.screen.LoadRoverImageScreen
import com.example.pequenoexploradorapp.presentation.screen.LoginScreen
import com.example.pequenoexploradorapp.presentation.screen.NasaVideoDetailScreen
import com.example.pequenoexploradorapp.presentation.screen.PictureOfTheDayScreen
import com.example.pequenoexploradorapp.presentation.screen.PlanetsScreen
import com.example.pequenoexploradorapp.presentation.screen.AvatarSelectionScreen
import com.example.pequenoexploradorapp.presentation.screen.ProfileScreen
import com.example.pequenoexploradorapp.presentation.screen.RoverMissionDetailScreen
import com.example.pequenoexploradorapp.presentation.screen.RoverMissionScreen
import com.example.pequenoexploradorapp.presentation.screen.RoverSearchImageScreen
import com.example.pequenoexploradorapp.presentation.screen.SearchImageScreen
import com.example.pequenoexploradorapp.presentation.screen.SearchNasaVideoScreen
import com.example.pequenoexploradorapp.presentation.screen.ShareFavouriteImageScreen
import com.example.pequenoexploradorapp.presentation.screen.SignInScreen
import com.example.pequenoexploradorapp.presentation.screen.SplashScreen
import com.example.pequenoexploradorapp.presentation.screen.WelcomeScreen
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun NavHostMain(
    navController: NavHostController = rememberNavController(),
    googleAuthUiClient: GoogleAuthUiClient,
    firebaseAnalytics: FirebaseAnalytics,
    context: Context
) {
    KoinContext {
        NavHost(
            navController = navController,
            startDestination = Route.LoginGraphNav.route,
            enterTransition = NavAnimations.slideLeftEnterAnimation,
            exitTransition = NavAnimations.slideLeftExitAnimation,
            popEnterTransition = NavAnimations.popEnterRightAnimation,
            popExitTransition = NavAnimations.popExitRightAnimation
        ) {
            loginNavGraph(
                navController,
                googleAuthUiClient,
                firebaseAnalytics,
                context
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.loginNavGraph(
    navController: NavHostController,
    googleAuthUiClient: GoogleAuthUiClient,
    firebaseAnalytics: FirebaseAnalytics,
    context: Context
) {
    navigation(
        route = Route.LoginGraphNav.route,
        startDestination = Route.SplashScreenRoute.route
    ) {
        composable(route = Route.SplashScreenRoute.route) {
            SplashScreen(
                onNavigateToWelcomeScreen = {
                    navController.navigate(Route.WelcomeScreenRoute.route) {
                        popUpTo(Route.SplashScreenRoute.route) {
                            inclusive = true
                        }
                    }

//                    navController.navigate(Route.HomeGraphNav.route) {
//                        popUpTo(Route.LoginScreenRoute.route) {
//                            inclusive = true
//                        }
//                    }
                }
            )
        }

        composable(route = Route.WelcomeScreenRoute.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.LoginScreenRoute.route)
                }
            )
        }

        composable(route = Route.LoginScreenRoute.route) {
            val viewModel: LoginUserViewModel = koinInject()
            val stateSignInGoogle by viewModel.stateSignInGoogle.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        coroutineScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onGoogleSignInResult(signInResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = Unit) {
                if (googleAuthUiClient.getSignedInUser() != null) {
                    Toast.makeText(
                        context,
                        "Sign in successful, navigate to Profile",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            LaunchedEffect(key1 = stateSignInGoogle.isSignInSuccessful) {
                if (stateSignInGoogle.isSignInSuccessful) {

                    val userGoogleData = googleAuthUiClient.getSignedInUser()
                    viewModel.saveUserGoogleData(userGoogleData)

                    Toast.makeText(
                        context,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetState()

                    navController.navigate(Route.HomeGraphNav.route) {
                        popUpTo(Route.LoginScreenRoute.route) {
                            inclusive = true
                        }
                    }
                }
            }

            LoginScreen(
                onGoogleSignInClick = {
                    coroutineScope.launch {
                        val signInIntentSender = googleAuthUiClient.signInGoogle()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Route.SignInScreenRoute.route)
                },
                onNavigateToHome = {
                    navController.navigate(Route.HomeGraphNav.route)
                }
            )
        }

        composable(route = Route.SignInScreenRoute.route) {
            SignInScreen(
                onNavigateToHome = {
                    navController.navigate(Route.HomeGraphNav.route)
                }
            )
        }

        homeNavGraph(firebaseAnalytics = firebaseAnalytics)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val params = Bundle()
            params.putString(FirebaseAnalytics.Param.SCREEN_NAME, destination.route)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        }
    }
}


private fun NavGraphBuilder.homeNavGraph(
    firebaseAnalytics: FirebaseAnalytics,
) {
    composable(
        route = Route.HomeGraphNav.route,
        enterTransition = NavAnimations.slideLeftEnterAnimation,
        exitTransition = NavAnimations.slideLeftExitAnimation,
        popEnterTransition = NavAnimations.popEnterRightAnimation,
        popExitTransition = NavAnimations.popExitRightAnimation
    ) {
        val navController = rememberNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val params = Bundle()
            params.putString(FirebaseAnalytics.Param.SCREEN_NAME, destination.route)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        }

        Column(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Route.HomeScreenRoute.route,
                Modifier.weight(1f)
            ) {
                composable(
                    route = Route.HomeScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    HomeMenuScreen(
                        onNavigateToSearchImage = {
                            navController.navigate(Route.SearchImageScreenRoute.route)
                        },
                        onNavigateToPictureOfTheDay = {
                            val date = getLocalDateFormattedApi()
                            val toolbar = "Destaque do Dia"
                            navController.navigate(
                                route = "${Route.PictureOfTheDayScreenRoute.route}/${date}/${toolbar}"
                            )
                        },
                        onNavigateToRoverMission = {
                            navController.navigate(Route.RoverMissionScreenRoute.route)
                        },
                        onNavigateToFavouriteImage = {
                            navController.navigate(Route.LoadFavouriteImageScreenRoute.route)
                        },
                        onNavigateToNasaVideos = {
                            navController.navigate(Route.SearchNasaVideosScreenRoute.route)
                        },
                        onNavigateToPlanets = {
                            navController.navigate(Route.PlanetsScreenRoute.route)
                        },
                        onNavigateToGallerySearch = {
                            navController.navigate(Route.GallerySearchScreenRoute.route)
                        },
                        onNavigateToProfile = {
                            navController.navigate(Route.ProfileScreenRoute.route)
                        }
                    )
                }

                composable(
                    route = Route.SearchImageScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    SearchImageScreen(
                        onNavigateToLoadNasaImage = { searchImage ->
                            navController.navigate(
                                route = "${Route.LoadNasaImageScreenRoute.route}/${searchImage}"
                            )
                        }
                    )
                }

                composable(
                    route = "${Route.LoadNasaImageScreenRoute.route}/{$IMAGE_SEARCH_KEY}",
                    arguments = listOf(
                        navArgument(IMAGE_SEARCH_KEY) {
                            type = NavType.StringType
                            defaultValue = "Moon"
                            nullable = false
                        },
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val imageSearch = arguments.getString(IMAGE_SEARCH_KEY)

                    LoadNasaImageScreen(
                        imageSearch = imageSearch,
                        onNavigateToSearchImage = {
                            navController.navigate(Route.SearchImageScreenRoute.route)
                        }
                    )
                }

                composable(
                    route = "${Route.PictureOfTheDayScreenRoute.route}/{$DATE_TO_SEARCH}/{$TOOLBAR}",
                    arguments = listOf(
                        navArgument(DATE_TO_SEARCH) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        },
                        navArgument(TOOLBAR) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        }
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val date = arguments.getString(DATE_TO_SEARCH) ?: getLocalDateFormattedApi()
                    val toolbarTitle = arguments.getString(TOOLBAR) ?: "Destaque do Dia"
                    PictureOfTheDayScreen(
                        date = date,
                        toolbarTitle = toolbarTitle,
                        onNavigateToHomeMenu = {
                            navController.navigate(Route.HomeScreenRoute.route)
                        }
                    )
                }

                composable(
                    route = Route.RoverMissionScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    RoverMissionScreen(
                        onNavigateToRoverSpirit = { idName, image ->
                            navController.navigate(
                                route = "${Route.RoverMissionDetailScreenRoute.route}/${idName}/${image}"
                            )
                        },
                        onNavigateToRoverCuriosity = { idName, image ->
                            navController.navigate(
                                route = "${Route.RoverMissionDetailScreenRoute.route}/${idName}/${image}"
                            )
                        },
                        onNavigateToRoverOpportunity = { idName, image ->
                            navController.navigate(
                                route = "${Route.RoverMissionDetailScreenRoute.route}/${idName}/${image}"
                            )
                        },
                        onNavigateToRoverPerseverance = { idName, image ->
                            navController.navigate(
                                route = "${Route.RoverMissionDetailScreenRoute.route}/${idName}/${image}"
                            )
                        }
                    )
                }

                composable(
                    route = "${Route.RoverMissionDetailScreenRoute.route}/{$ID_NAME_KEY}/{$IMAGE_ROVER}",
                    arguments = listOf(
                        navArgument(ID_NAME_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        },
                        navArgument(IMAGE_ROVER) {
                            type = NavType.IntType
                            defaultValue = 0
                            nullable = false
                        }
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val idName = arguments.getString(ID_NAME_KEY).toString()
                    val image = arguments.getInt(IMAGE_ROVER)

                    RoverMissionDetailScreen(
                        roverName = idName,
                        image = image,
                        onNavigateToSearchImage = { initialDate, finalDate, nameRover ->
                            navController.navigate(
                                route = "${Route.RoverSearchImageScreenRoute.route}/${initialDate}/${finalDate}/${nameRover}"
                            )
                        },
                        onNavigateToHomeMenu = {
                            navController.navigate(Route.HomeScreenRoute.route)
                        }
                    )
                }

                composable(
                    route = "${Route.RoverSearchImageScreenRoute.route}/{$DATE_INITIAL_KEY}/{$DATE_FINAL_KEY}/{$ID_NAME_KEY}",
                    arguments = listOf(
                        navArgument(DATE_INITIAL_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        },
                        navArgument(DATE_FINAL_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        },
                        navArgument(ID_NAME_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        }
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val dateInitial = arguments.getString(DATE_INITIAL_KEY).toString()
                    val dateFinal = arguments.getString(DATE_FINAL_KEY).toString()
                    val nameRover = arguments.getString(ID_NAME_KEY).toString()

                    RoverSearchImageScreen(
                        nameRover = nameRover,
                        dateInitial = dateInitial,
                        dateFinal = dateFinal,
                        onNavigateToLoadRoverImage = { date, rover ->
                            navController.navigate(
                                route = "${Route.LoadRoverImageScreenRoute.route}/${date}/${nameRover}"
                            )
                        }
                    )
                }

                composable(
                    route = "${Route.LoadRoverImageScreenRoute.route}/{$DATE_KEY}/{$ID_NAME_KEY}",
                    arguments = listOf(
                        navArgument(DATE_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        },
                        navArgument(ID_NAME_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        },
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val date = arguments.getString(DATE_KEY).toString()
                    val nameRover = arguments.getString(ID_NAME_KEY).toString()

                    LoadRoverImageScreen(
                        date = date,
                        nameRover = nameRover,
                        onNavigateToHomeMenu = {
                            navController.navigate(Route.HomeScreenRoute.route)
                        }
                    )
                }

                composable(
                    route = Route.LoadFavouriteImageScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    LoadFavouriteImageScreen(
                        onNavigateToHomeMenu = {
                            navController.navigate(Route.HomeScreenRoute.route)
                        },
                        onNavigateToShareImage = { image ->
                            val encodeImagedUrl =
                                URLEncoder.encode(image, StandardCharsets.UTF_8.toString())
                            navController.navigate(
                                route = "${Route.ShareFavouriteImageScreenRoute.route}/${encodeImagedUrl}"
                            )
                        }
                    )
                }

                composable(
                    route = "${Route.ShareFavouriteImageScreenRoute.route}/{$IMAGE_TO_SHARE}",
                    arguments = listOf(
                        navArgument(IMAGE_TO_SHARE) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        }
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val imageUrl = arguments.getString(IMAGE_TO_SHARE).toString()

                    ShareFavouriteImageScreen(
                        image = imageUrl
                    )
                }

                composable(
                    route = Route.SearchNasaVideosScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    SearchNasaVideoScreen(
                        onNavigateToLoadNasaVideo = { searchVideo ->
                            navController.navigate(
                                route = "${Route.LoadNasaVideoScreenRoute.route}/${searchVideo}"
                            )
                        }
                    )
                }

                composable(
                    route = "${Route.LoadNasaVideoScreenRoute.route}/{$VIDEO_SEARCH_KEY}",
                    arguments = listOf(
                        navArgument(VIDEO_SEARCH_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        }
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val searchVideo = arguments.getString(VIDEO_SEARCH_KEY).toString()

                    LoadNasaVideoScreen(
                        video = searchVideo,
                        onNavigateToSearchVideo = { },
                        onNavigateToVideoDetail = { url ->
                            val encodeImagedUrl =
                                URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                            navController.navigate(
                                route = "${Route.NasaVideoDetailScreenRoute.route}/${encodeImagedUrl}"
                            )
                        }
                    )
                }

                composable(
                    route = "${Route.NasaVideoDetailScreenRoute.route}/{$VIDEO_TO_LOAD}",
                    arguments = listOf(
                        navArgument(VIDEO_TO_LOAD) {
                            type = NavType.StringType
                            defaultValue = ""
                            nullable = false
                        }
                    ),
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val video = arguments.getString(VIDEO_TO_LOAD).toString()

                    NasaVideoDetailScreen(
                        video = video
                    )
                }

                composable(
                    route = Route.PlanetsScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    PlanetsScreen(
                        onNavigateToLoadNasaImage = {}
                    )
                }

                composable(
                    route = Route.GallerySearchScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    val toolbar = "Galeria"
                    GallerySearchScreen(
                        onNavigateToLoadImage = { date ->
                            navController.navigate(
                                route = "${Route.PictureOfTheDayScreenRoute.route}/${date}/${toolbar}"
                            )
                        }
                    )
                }

                composable(
                    route = Route.AvatarSelectionScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    AvatarSelectionScreen(
                        onNavigateToProfile ={
                            navController.navigate(Route.ProfileScreenRoute.route)
                        }
                    )
                }

                composable(
                    route = Route.ProfileScreenRoute.route,
                    enterTransition = NavAnimations.slideLeftEnterAnimation,
                    exitTransition = NavAnimations.slideLeftExitAnimation,
                    popEnterTransition = NavAnimations.popEnterRightAnimation,
                    popExitTransition = NavAnimations.popExitRightAnimation
                ) {
                    ProfileScreen(
                        onNavigateBack = {
                            navController.navigate(Route.AvatarSelectionScreenRoute.route)
                        },
                        onNavigateToChooseAvatar = {
                            navController.navigate(Route.AvatarSelectionScreenRoute.route)
                        }
                    )
                }
            }
            val items = remember {
                listOf(
                    NavItem.Home,
                    NavItem.IMAGES,
                    NavItem.FAVOURITE,
                )
            }
            AppBottomNavigationBar(
                navItems = items,
                navController = navController
            )
        }
    }
}

object ArgumentsKey {
    const val IMAGE_SEARCH_KEY = "IMAGE_SEARCH_KEY"
    const val VIDEO_SEARCH_KEY = "VIDEO_SEARCH_KEY"
    const val ID_NAME_KEY = "ID_NAME_KEY"
    const val DATE_KEY = "DATE_KEY"
    const val DATE_INITIAL_KEY = "DATE_INITIAL_KEY"
    const val DATE_FINAL_KEY = "DATE_FINAL_KEY"
    const val IMAGE_TO_SHARE = "IMAGE_TO_SHARE"
    const val VIDEO_TO_LOAD = "VIDEO_TO_LOAD"
    const val DATE_TO_SEARCH = "DATE_TO_SEARCH"
    const val TOOLBAR = "TOOLBAR"
    const val IMAGE_ROVER = "IMAGE_ROVER"
}
