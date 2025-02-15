package com.example.pequenoexploradorapp.presentation.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.SubcomposeAsyncImage
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.PictureOfTheDay
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedDate
import com.example.pequenoexploradorapp.domain.util.toHttpsPrefix
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.VerticalSpacer
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.PictureOfTheDayViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.PictureOfTheDayViewState
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictureOfTheDayScreen(
    date: String,
    toolbarTitle: String,
    onNavigateToHomeMenu: () -> Unit,
    viewModel: PictureOfTheDayViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    var snackBarIsActivated by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            MenuToolbar(
                title = toolbarTitle,
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
            is PictureOfTheDayViewState.Init -> {
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
                        color = mainColor
                    )
                }
                viewModel.onPictureOfTheDayRequest(date)
            }

            is PictureOfTheDayViewState.Success -> {
                RenderSuccessLoadImage(
                    paddingValues = paddingValues,
                    viewModel = viewModel,
                    image = state.image,
                    isLoading = false
                )
            }

            is PictureOfTheDayViewState.SuccessVideoUrl -> {
                WebView(
                    videoUrl = state.videoUrl.url.toString(),
                    explanation = state.videoUrl.explanation.toString()
                )
            }

            is PictureOfTheDayViewState.SaveFavourite -> {
                RenderSuccessLoadImage(
                    paddingValues = paddingValues,
                    viewModel = viewModel,
                    image = state.image,
                    isLoading = state.isLoading
                )
            }

            is PictureOfTheDayViewState.Error -> {
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
                            onNavigateToHomeMenu()
                        }
                    }
                }
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

@Composable
fun RenderSuccessLoadImage(
    paddingValues: PaddingValues,
    viewModel: PictureOfTheDayViewModel,
    image: PictureOfTheDay,
    isLoading: Boolean,
) {
    var isPressed by remember { mutableStateOf(false) }
    val isFavourite = image.isFavourite
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isFavourite) 1.5f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = ""
    )
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(600L)
            image.isFavourite = true
            isPressed = false
            val favourite = FavouriteImageToSave(
                id = System.currentTimeMillis(),
                title = image.title,
                dateCreated = image.date?.formattedDate(),
                link = image.url,
                creators = null,
                keywords = null,
                isFavourite = true
            )
            viewModel.onSaveFavourite(
                favourite,
                image
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .paint(
                painterResource(id = R.drawable.simple_background),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VerticalSpacer(16.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(
                        width = 1.dp,
                        color = ListItemDefaults.contentColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { },
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box {
                    SubcomposeAsyncImage(
                        model = image.hdUrl?.toHttpsPrefix(),
                        loading = {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                    color = mainColor
                                )
                            }
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    )
                    IconButton(
                        onClick = { isPressed = true },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .scale(scale)
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = if (image.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Image from Picture Of the Day",
                            tint = mainColor
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    text = "Imagem: ${image.explanation}",
                    textAlign = TextAlign.Justify,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            VerticalSpacer(16.dp)
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
                color = mainColor
            )
        }
    }
}


@Composable
fun VideoPlayer(videoUrl: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
            play()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = { onClose() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(videoUrl: String, explanation: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .paint(
                painterResource(id = R.drawable.simple_background),
                contentScale = ContentScale.FillBounds
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(16.dp)
        Box(
            modifier = Modifier
                .height(450.dp)
                .background(Color.Black.copy(alpha = 0.8f))
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.domStorageEnabled = true
                        settings.javaScriptEnabled = true
                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = {
                    it.loadUrl(videoUrl)
                    it.setBackgroundColor(Color.Black.copy(alpha = 0.8f).toArgb())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f))
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(
                    width = 1.dp,
                    color = ListItemDefaults.contentColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { },
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                text = "Imagem: $explanation",
                textAlign = TextAlign.Justify,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
    VerticalSpacer(10.dp)
}

class CustomWebChromeClient : WebChromeClient() {
    override fun onCloseWindow(window: WebView?) {}

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return false
    }
}