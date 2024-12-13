package com.example.pequenoexploradorapp.presentation.navigation

import android.app.Activity
import android.content.Context
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.pequenoexploradorapp.domain.util.GoogleAuthUiClient
import com.example.pequenoexploradorapp.presentation.components.AppBottomNavigationBar
import com.example.pequenoexploradorapp.presentation.screen.HomeMenuScreen
import com.example.pequenoexploradorapp.presentation.screen.LoginScreen
import com.example.pequenoexploradorapp.presentation.screen.SearchImageScreen
import com.example.pequenoexploradorapp.presentation.screen.SignInScreen
import com.example.pequenoexploradorapp.presentation.screen.SplashScreen
import com.example.pequenoexploradorapp.presentation.screen.WelcomeScreen
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject


@Composable
fun NavHostMain(
    navController: NavHostController = rememberNavController(),
    googleAuthUiClient: GoogleAuthUiClient,
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
                context
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.loginNavGraph(
    navController: NavHostController,
    googleAuthUiClient: GoogleAuthUiClient,
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

//                    navController.navigate(Route.HomeGraphNav.route){
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
                    navController.navigate(Route.HomeGraphNav.route)
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

        homeNavGraph()
    }
}


private fun NavGraphBuilder.homeNavGraph() {
    composable(
        route = Route.HomeGraphNav.route,
        enterTransition = NavAnimations.slideLeftEnterAnimation,
        exitTransition = NavAnimations.slideLeftExitAnimation,
        popEnterTransition = NavAnimations.popEnterRightAnimation,
        popExitTransition = NavAnimations.popExitRightAnimation
    ) {
        val navController = rememberNavController()
        val items = remember {
            listOf(
                NavItem.Home,
                NavItem.IMAGES,
                NavItem.PROFILE,
            )
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
                    SearchImageScreen()
                }
            }

            AppBottomNavigationBar(
                navItems = items,
                navController = navController
            )
        }
    }
}
