package com.example.pequenoexploradorapp.presentation.navigation

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pequenoexploradorapp.presentation.screen.LoginScreen
import com.example.pequenoexploradorapp.presentation.screen.SignInScreen
import com.example.pequenoexploradorapp.presentation.screen.SplashScreen
import com.example.pequenoexploradorapp.presentation.screen.WelcomeScreen
import com.example.pequenoexploradorapp.domain.util.GoogleAuthUiClient
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHostMain(
    navController: NavHostController = rememberNavController(),
    googleAuthUiClient: GoogleAuthUiClient,
    context: Context
) {
    KoinContext {
        NavHost(
            navController = navController,
            startDestination = SplashScreenRoute,
            enterTransition = NavAnimations.slideLeftEnterAnimation,
            exitTransition = NavAnimations.slideLeftExitAnimation,
            popEnterTransition = NavAnimations.popEnterRightAnimation,
            popExitTransition = NavAnimations.popExitRightAnimation
        ) {
            composable<SplashScreenRoute> {
                SplashScreen(
                    onNavigateToWelcomeScreen = {
                        navController.navigate(WelcomeScreenRoute)
                    }
                )
            }

            composable<WelcomeScreenRoute> {
                WelcomeScreen(
                    onNavigateToLogin = {
                        navController.navigate(LoginScreenRoute)
                    }
                )
            }

            composable<LoginScreenRoute> {
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

                        navController.navigate(SignInScreenRoute)
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
                        navController.navigate(SignInScreenRoute)
                    },
                    onNavigateToHome = {},
                )


            }

            composable<SignInScreenRoute> {
                SignInScreen(
                    onNavigateToHome = {}
                )
            }
        }
    }
}
