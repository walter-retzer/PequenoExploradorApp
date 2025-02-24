package com.example.pequenoexploradorapp.presentation.screen

import android.util.Log
import android.webkit.URLUtil
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink80
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.NasaVideoDetailViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.NasaVideoDetailViewState
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NasaVideoDetailScreen(
    video: String,
    viewModel: NasaVideoDetailViewModel = koinInject()
) {
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading by remember { mutableStateOf(false) }
    var snackBarIsActivated by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    contentColor = mainColor,
                    snackbarData = data
                )
            }
        },
        topBar = {
            MenuToolbar(
                title = "Detalhe",
                onNavigationToMenu = { },
                onNavigationToProfile = { },
                onNavigateToNotifications = { },
                toolbarBehavior = toolbarBehavior,
                isActivatedBadge = false
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        when (val state = uiState) {
            is NasaVideoDetailViewState.Error -> {
                snackBarIsActivated = state.isActivated
                if (snackBarIsActivated) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .paint(
                                painterResource(id = R.drawable.simple_background),
                                contentScale = ContentScale.FillBounds
                            )
                    ) {
                        LaunchedEffect(Unit) {
                            snackBarOnlyMessage(
                                snackBarHostState = snackBarHostState,
                                coroutineScope = scope,
                                message = state.message,
                                duration = SnackbarDuration.Long
                            )
                            snackBarIsActivated = false
                            delay(3000L)
                        }
                    }
                }
            }

            is NasaVideoDetailViewState.Init -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .paint(
                            painterResource(id = R.drawable.simple_background),
                            contentScale = ContentScale.FillBounds
                        )
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .align(Alignment.Center),
                        color = Pink80
                    )
                }
                viewModel.onVideoUrlToLoad(video)
            }

            is NasaVideoDetailViewState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .paint(
                            painterResource(id = R.drawable.simple_background),
                            contentScale = ContentScale.FillBounds
                        ),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                   Row {
                        if (URLUtil.isValidUrl(state.video)) {
                            val exoPlayer = ExoPlayer.Builder(context).build()
                            LaunchedEffect(exoPlayer) {
                                try {
                                    val mediaItem = MediaItem.Builder()
                                        .setUri(state.video)
                                        .build()
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.prepare()
                                    viewModel.restorePlaybackPosition(exoPlayer)
                                    exoPlayer.playWhenReady = viewModel.playerState

                                } catch (e: Exception) {
                                    Log.d(
                                        "ExoPlayer Error",
                                        exoPlayer.playerError.toString()
                                    )
                                }
                            }
                            AndroidView(
                                factory = {
                                    PlayerView(context).apply {
                                        player = exoPlayer
                                    }
                                },
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .semantics { testTag = "Card Media Player" }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DisposableEffect(exoPlayer) {
                                onDispose {
                                    viewModel.savePlaybackPosition(exoPlayer)
                                    viewModel.playerState = exoPlayer.playWhenReady
                                    exoPlayer.stop()
                                    exoPlayer.release()
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .paint(
                                        painterResource(id = R.drawable.simple_background),
                                        contentScale = ContentScale.FillBounds
                                    )
                            ) {
                                LaunchedEffect(Unit) {
                                    snackBarOnlyMessage(
                                        snackBarHostState = snackBarHostState,
                                        coroutineScope = scope,
                                        message = ConstantsApp.VIDEO_ERROR_LOAD,
                                        duration = SnackbarDuration.Long
                                    )
                                    snackBarIsActivated = false
                                    delay(3000L)
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(64.dp)
                                .align(Alignment.Center),
                            color = Pink80
                        )
                    }
                }
                if (isConnected == false && !snackBarIsActivated) {
                    LaunchedEffect(Unit) {
                        snackBarOnlyMessage(
                            snackBarHostState = snackBarHostState,
                            coroutineScope = scope,
                            message = ConstantsApp.ERROR_WITHOUT_INTERNET,
                            duration = SnackbarDuration.Long
                        )
                        snackBarIsActivated = false
                    }
                }
            }
        }
    }
}
