package com.example.pequenoexploradorapp.presentation.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.firebase.FirebaseRemoteConfigManager
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.ConstantsApp.Companion.TAG_FIREBASE_MESSAGING
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun SplashScreen(
    onNavigateToWelcomeScreen: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val scale = remember { Animatable(0f) }
    val animationDelay = 1500
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    LaunchedEffect(key1 = true) {
        delay(5000L)
        FirebaseRemoteConfigManager.fetchRemoteConfig { isSuccess ->
            if (isSuccess) {
                scope.launch {
                    val token = Firebase.messaging.token.await()
                    Log.d(TAG_FIREBASE_MESSAGING, "FCM token: $token")
                }
                onNavigateToWelcomeScreen()
            } else snackBarOnlyMessage(
                snackBarHostState = snackBarHostState,
                coroutineScope = scope,
                message = ConstantsApp.ERROR_REMOTE_CONFIG,
                duration = SnackbarDuration.Long
            )
        }
    }
    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = true) {
            delay((animationDelay / 3L) * (index + 1))
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.simple_background),
                contentScale = ContentScale.FillBounds
            )
            .background(color = Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        circles.forEach { animatable ->
            Box(
                modifier = Modifier
                    .scale(scale = animatable.value)
                    .size(360.dp)
                    .clip(shape = CircleShape)
                    .background(
                        color = Color.White.copy(alpha = (1 - animatable.value)),
                        CircleShape
                    )
            )
        }
        Image(
            painter = painterResource(R.drawable.perfil01),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .background(Color.Black, CircleShape)
        )
    }
}
