package com.example.pequenoexploradorapp.presentation.screen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    onNavigateToWelcomeScreen: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val animationDelay = 900
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    LaunchedEffect(key1 = true) {
        delay(5000L)
        onNavigateToWelcomeScreen()
    }

    circles.forEach { animatable ->
        LaunchedEffect(key1 = true) {
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
            ),
        contentAlignment = Alignment.Center
    ) {
        circles.forEach { animatable ->
            Box(
                modifier = Modifier
                    .scale(scale = animatable.value)
                    .size(size = 400.dp)
                    .clip(shape = CircleShape)
                    .background(
                        color = Color.White
                            .copy(alpha = (1 - animatable.value))
                    )
            )
        }

        Image(
            painter = painterResource(R.drawable.perfil01),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .scale(scale.value)
                .size(200.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .background(Color.Black, CircleShape)
        )
    }
}
