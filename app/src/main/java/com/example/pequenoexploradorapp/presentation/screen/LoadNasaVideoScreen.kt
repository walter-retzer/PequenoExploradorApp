package com.example.pequenoexploradorapp.presentation.screen

import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
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
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.mainColor
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
                    context = context,
                    listOfVideosFromApi = state.listOfNasaImage,
                    viewModel = viewModel,
                    isLoading = state.isLoading,
                    isLoadingNextItems = false,
                    totalHits = state.totalHits,
                )
            }

            is LoadNasaVideoViewState.SuccessVideo -> {
                RenderVideoSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    context = context,
                    listOfVideosFromApi = state.video,
                    viewModel = viewModel,
                    isLoading = false,
                    isLoadingNextItems = true,
                    totalHits = state.totalHits,
                )

            }

            is LoadNasaVideoViewState.SuccessLoadMoreVideos -> {
                RenderVideoSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    context = context,
                    listOfVideosFromApi = state.updateListOfVideos,
                    viewModel = viewModel,
                    isLoading = false,
                    isLoadingNextItems = true,
                    totalHits = state.totalHits,
                )
            }

            is LoadNasaVideoViewState.SuccessAddFavourite -> {
                RenderVideoSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    context = context,
                    listOfVideosFromApi = state.updateListOfImageFavourite,
                    viewModel = viewModel,
                    isLoading = false,
                    isLoadingNextItems = false,
                    totalHits = state.totalHits,
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


//val u = "https://images-assets.nasa.gov/video/NHQ_2019_0311_Go Forward to the Moon/NHQ_2019_0311_Go Forward to the Moon~large.mp4"



@Composable
fun InfiniteVideoListHandler(
    listState: LazyGridState,
    isLoadingNextItems: Boolean,
    listOfImagesFromApi: List<NasaImageItems>,
    totalHits: Int,
    numberOfVideo: Int,
    buffer: Int = 6,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (totalItemsCount - buffer)
            //&& isLoadingNextItems && totalHits != listOfImagesFromApi.size
        }
    }

//    val shouldLoadMore = remember {
//        derivedStateOf {
//            listOfImagesFromApi.size - buffer == (numberOfVideo)
//
//        }
//    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onLoadMore()
            }
    }
}



@Composable
fun RenderVideoSuccess(
    paddingValues: PaddingValues,
    scrollState: LazyGridState,
    scope: CoroutineScope,
    listOfVideosFromApi: List<NasaImageItems>,
    viewModel: LoadNasaVideoViewModel,
    context: Context,
    isLoading: Boolean,
    isLoadingNextItems: Boolean,
    totalHits: Int,
) {
    var numberOfVideo = 0
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
                text = "Foram encontradas ${totalHits} videos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Justify,
                color = Color.White
            )
        }
        LazyVerticalGrid(
            state = scrollState,
            contentPadding = PaddingValues(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            columns = GridCells.Fixed(2),
            modifier = Modifier.clipToBounds(),
        ) {
            items(listOfVideosFromApi.size) { numberOfImage ->
                numberOfVideo = numberOfImage
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
                        text = "list: ${listOfVideosFromApi.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Justify,
                        color = Color.White
                    )
                }
                Row{
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                scope.launch {
                                    scrollState.animateScrollToItem(0)
                                }
                            },
                        text = "Video: ${numberOfImage + 1}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Justify,
                        color = Color.White
                    )
                }
                Row {
                    val url = listOfVideosFromApi[numberOfImage].href ?: ""

                    val exoPlayer = ExoPlayer.Builder(context).build()
                    LaunchedEffect(Unit) {
                        val response = viewModel.onVideoUrlToLoad(url)
                        if (URLUtil.isValidUrl(response)) {
                            try {
                                val mediaItem = MediaItem.Builder()
                                    .setUri(response)
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
                }
                Row{
                    val isFavourite = listOfVideosFromApi[numberOfImage].isFavourite
                    IconButton(
                        onClick = {  },
                        modifier = Modifier
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Nasa Image",
                            tint = mainColor
                        )
                    }
                }

            }
        }
        InfiniteVideoListHandler(
            listState = scrollState,
            isLoadingNextItems = isLoadingNextItems,
            listOfImagesFromApi = listOfVideosFromApi,
            totalHits = totalHits,
            numberOfVideo = numberOfVideo,
            onLoadMore = {
                viewModel.loadNextImage()
                println("loadNextVideos")
            }
        )
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
