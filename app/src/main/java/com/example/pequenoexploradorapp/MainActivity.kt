package com.example.pequenoexploradorapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pequenoexploradorapp.navigation.LoginScreenRoute
import com.example.pequenoexploradorapp.navigation.SplashScreenRoute
import com.example.pequenoexploradorapp.navigation.WelcomeScreenRoute
import com.example.pequenoexploradorapp.screen.LoginScreen
import com.example.pequenoexploradorapp.screen.SplashScreen
import com.example.pequenoexploradorapp.screen.WelcomeScreen
import com.example.pequenoexploradorapp.ui.theme.PequenoExploradorAppTheme
import com.example.pequenoexploradorapp.util.GoogleAuthUiClient
import com.example.pequenoexploradorapp.viewmodel.LoginUserViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = actionBar
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        enableEdgeToEdge()

        setContent {
            PequenoExploradorAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = SplashScreenRoute
                ) {
                    composable<SplashScreenRoute> {
                        SplashScreen(
                            onNavigateToWelcomeScreen = {
                                navController.navigate(WelcomeScreenRoute)
                            }
                        )
                    }

                    composable<WelcomeScreenRoute> {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            WelcomeScreen(
                                innerPadding = innerPadding,
                                onNavigateToLogin = {
                                    navController.navigate(LoginScreenRoute)
                                }
                            )
                        }
                    }

                    composable<LoginScreenRoute> {
                        val viewModel: LoginUserViewModel = koinInject()
                        val stateSignInGoogle by viewModel.stateSignInGoogle.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthUiClient.getSignedInUser() != null) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful, navigate to Profile",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        viewModel.onGoogleSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = stateSignInGoogle.isSignInSuccessful) {
                            if (stateSignInGoogle.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                viewModel.resetState()
                            }
                        }

                        LoginScreen(
                            onNavigateToHome = {},
                            onGoogleSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signInGoogle()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PequenoExploradorAppTheme {
        SplashScreen(
            onNavigateToWelcomeScreen = {}
        )
    }
}
