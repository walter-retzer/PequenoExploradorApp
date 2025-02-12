package com.example.pequenoexploradorapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.pequenoexploradorapp.domain.util.GoogleAuthUiClient
import com.example.pequenoexploradorapp.presentation.navigation.NavHostMain
import com.example.pequenoexploradorapp.presentation.screen.SplashScreen
import com.example.pequenoexploradorapp.presentation.theme.PequenoExploradorAppTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.Black.toArgb()
        window.navigationBarColor = Color.Black.toArgb()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setContent {
            PequenoExploradorAppTheme {
                val navController = rememberNavController()
                NavHostMain(
                    navController = navController,
                    googleAuthUiClient = googleAuthUiClient,
                    firebaseAnalytics = firebaseAnalytics,
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
