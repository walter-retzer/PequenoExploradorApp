package com.example.pequenoexploradorapp.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.pequenoexploradorapp.presentation.navigation.NavHostMain
import com.example.pequenoexploradorapp.presentation.screen.SplashScreen
import com.example.pequenoexploradorapp.presentation.theme.PequenoExploradorAppTheme
import com.example.pequenoexploradorapp.domain.util.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity


class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = actionBar
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            PequenoExploradorAppTheme {
                val navController = rememberNavController()
                NavHostMain(
                    navController = navController,
                    googleAuthUiClient = googleAuthUiClient,
                    context = applicationContext
                )
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
