package com.example.pequenoexploradorapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pequenoexploradorapp.navigation.SplashScreenRoute
import com.example.pequenoexploradorapp.navigation.WelcomeScreenRoute
import com.example.pequenoexploradorapp.screen.SplashScreen
import com.example.pequenoexploradorapp.screen.WelcomeScreen
import com.example.pequenoexploradorapp.ui.theme.PequenoExploradorAppTheme


class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = actionBar
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor= Color.BLACK
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
                        Scaffold(modifier = Modifier.fillMaxSize()) {
                            WelcomeScreen()
                        }
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
