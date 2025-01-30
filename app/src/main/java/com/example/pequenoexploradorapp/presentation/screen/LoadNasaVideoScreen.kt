package com.example.pequenoexploradorapp.presentation.screen

import android.util.Log
import android.webkit.URLUtil
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults.contentColor
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.SubcomposeAsyncImage
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedDate
import com.example.pequenoexploradorapp.domain.util.toHttpsPrefix
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.theme.primaryDark
import com.example.pequenoexploradorapp.presentation.theme.surfaceDark
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewState
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaVideoViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaVideoViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadNasaVideoScreen(
    video: String?,
    onNavigateToSearchImage: () -> Unit,
    viewModel: LoadNasaVideoViewModel = koinInject()
) {
    val scrollState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var snackBarIsActivated by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            MenuToolbar(
                title = "Imagens",
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
            is LoadNasaVideoViewState.Init -> {
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
                viewModel.onNasaVideoSearch(video)
            }

            is LoadNasaVideoViewState.Loading -> {
                RenderVideoSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.listOfNasaImage,
                    viewModel = viewModel,
                    isLoading = state.isLoading,
                    isLoadingNextItems = false,
                    totalHits = state.totalHits,
                    url = ""
                )
            }

            is LoadNasaVideoViewState.Success -> {
                RenderVideoSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.images,
                    viewModel = viewModel,
                    isLoading = false,
                    isLoadingNextItems = true,
                    totalHits = state.totalHits,
                    url = ""
                )
            }

            is LoadNasaVideoViewState.SuccessAddFavourite -> {
                RenderVideoSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.updateListOfImageFavourite,
                    viewModel = viewModel,
                    isLoading = false,
                    isLoadingNextItems = true,
                    totalHits = state.totalHits,
                    url = ""
                )
            }

            is LoadNasaVideoViewState.Error -> {
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
                            onNavigateToSearchImage()
                        }
                    }
                }
            }

            is LoadNasaVideoViewState.SuccessVideo -> {
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
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    scope.launch {
                                        scrollState.animateScrollToItem(0)
                                    }
                                },
                            text = "Foram encontradas ${state.totalHits} videos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Justify,
                            color = Color.White
                        )
                    }
                    //val u = "https://images-assets.nasa.gov/video/NHQ_2019_0311_Go Forward to the Moon/NHQ_2019_0311_Go Forward to the Moon~large.mp4"
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

                                } catch (e: Exception) {
                                    Log.d("ExoPlayer Error", exoPlayer.playerError.toString())
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

                            DisposableEffect(exoPlayer) {
                                onDispose {
                                    exoPlayer.stop()
                                    exoPlayer.release()
                                }
                            }
                        } else {
                            Text(text = "Something went wrong")
                        }
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

@Composable
fun RenderVideoSuccess(
    paddingValues: PaddingValues,
    scrollState: LazyGridState,
    scope: CoroutineScope,
    listOfImagesFromApi: List<NasaImageItems>,
    viewModel: LoadNasaVideoViewModel,
    isLoading: Boolean,
    isLoadingNextItems: Boolean,
    url: String,
    totalHits: Int
) {
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
        if (totalHits == 0) {
            Box {
                AnimatedLottieFile(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(300.dp)
                        .align(Alignment.TopCenter),
                    file = R.raw.animation_telescopy
                )
            }
            Row {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = "Infelizmente, não foi possível encontrar as imagens pesquizadas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Justify,
                    color = Color.White
                )
            }
        } else {
            Row {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            scope.launch {
                                scrollState.animateScrollToItem(0)
                            }
                        },
                    text = "Foram encontradas $totalHits videos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Justify,
                    color = Color.White
                )
            }
        }
    }
}
