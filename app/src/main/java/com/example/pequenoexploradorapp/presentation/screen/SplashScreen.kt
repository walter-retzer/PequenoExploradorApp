package com.example.pequenoexploradorapp.presentation.screen

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scale = remember { Animatable(0f) }
    val animationDelay = 1500
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )
    var init by remember { mutableStateOf(false) }

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationsPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    snackBarOnlyMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = scope,
                        message = ConstantsApp.NOTIFICATION_DENIED,
                        duration = SnackbarDuration.Long
                    )
                    init = true
                } else {
                    snackBarOnlyMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = scope,
                        message = ConstantsApp.NOTIFICATION_DENIED,
                        duration = SnackbarDuration.Long
                    )
                    init = true
                }
            }
        )
        val isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            LocalContext.current,
            POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        val isShouldShowRequestPermissionRationale = LocalActivity.current?.let {
            shouldShowRequestPermissionRationale(it, POST_NOTIFICATIONS)
        } ?: false

        if (!isNotificationPermissionGranted && !isShouldShowRequestPermissionRationale) {
            SideEffect { notificationsPermissionResultLauncher.launch(POST_NOTIFICATIONS) }
        }
    }

    if (init) LaunchedEffect(Unit) {
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
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "$init ",
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Justify,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
