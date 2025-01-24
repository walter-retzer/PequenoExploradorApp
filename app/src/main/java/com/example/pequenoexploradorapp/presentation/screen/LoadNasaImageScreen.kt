package com.example.pequenoexploradorapp.presentation.screen

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.ImageToLoad
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedDate
import com.example.pequenoexploradorapp.domain.util.snackBarOnlyMessage
import com.example.pequenoexploradorapp.domain.util.toHttpsPrefix
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.theme.primaryLight
import com.example.pequenoexploradorapp.presentation.theme.secondaryLight
import com.example.pequenoexploradorapp.presentation.theme.surfaceDark
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadNasaImageScreen(
    imageSearch: String?,
    viewModel: LoadNasaImageViewModel = koinInject()
) {
    val scrollState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listImages by viewModel.imageListFlow.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    var progressButtonIsActivated by remember { mutableStateOf(false) }
    var snackBarIsActivated by remember { mutableStateOf(false) }
    var totalHits by remember { mutableStateOf(0) }

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
            is LoadNasaImageViewState.Init -> {
                viewModel.onNasaImageSearch(imageSearch)
            }

            is LoadNasaImageViewState.Loading -> {
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
            }

            is LoadNasaImageViewState.LoadingFavourite -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfNasaImages = listImages,
                    viewModel = viewModel,
                    isLoading = state.isLoading,
                    totalHits = totalHits
                )
            }

            is LoadNasaImageViewState.Error -> {
                progressButtonIsActivated = false
                snackBarIsActivated = true
                LaunchedEffect(snackBarIsActivated) {
                    snackBarOnlyMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = scope,
                        message = state.message,
                        duration = SnackbarDuration.Long
                    )
                    snackBarIsActivated = false
                }
            }

            is LoadNasaImageViewState.Success -> {
                totalHits = state.images.collection.metadata?.totalHits ?: 0
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfNasaImages = listImages,
                    viewModel = viewModel,
                    isLoading = isLoading,
                    totalHits = totalHits
                )
            }

            is LoadNasaImageViewState.SuccessFavourite -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfNasaImages = state.list,
                    viewModel = viewModel,
                    isLoading = isLoading,
                    totalHits = totalHits
                )
            }
        }
    }

    if (isConnected?.not() == true) {
        snackBarIsActivated = true
        LaunchedEffect(snackBarIsActivated) {
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
fun RenderSuccess(
    paddingValues: PaddingValues,
    scrollState: LazyGridState,
    scope: CoroutineScope,
    listOfNasaImages: List<NasaImageItems>,
    viewModel: LoadNasaImageViewModel,
    isLoading: Boolean,
    totalHits: Int
) {
    var page by remember { mutableStateOf(1) }
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
                    text = "Foram encontradas $totalHits imagens",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Justify,
                    color = Color.White
                )
            }
        }
        Row {
            LazyVerticalGrid(
                state = scrollState,
                contentPadding = PaddingValues(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Fixed(2),
                modifier = Modifier.clipToBounds(),
            ) {
                items(listOfNasaImages.size) { numberOfImage ->
                    LoadImageOnCard(
                        listOfImages = listOfNasaImages,
                        numberOfImage = numberOfImage,
                        viewModel = viewModel
                    )
                }
            }
        }
        // Handle infinite scrolling
        InfiniteListHandler(
            listState = scrollState,
            onLoadMore = {
                page++
                viewModel.loadNextItems(
                    page = page
                )
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

@Composable
fun InfiniteListHandler(
    listState: LazyGridState,
    buffer: Int = 10,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (totalItemsCount - buffer)
        }
    }

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
fun LoadImageOnCard(
    listOfImages: List<NasaImageItems>?,
    numberOfImage: Int,
    viewModel: LoadNasaImageViewModel
) {
    val title = listOfImages?.get(numberOfImage)?.data?.first()?.title
    val date = "Data: ${listOfImages?.get(numberOfImage)?.data?.first()?.dateCreated?.formattedDate()}"
    val imageUrl = listOfImages?.get(numberOfImage)?.links?.first()?.href?.toHttpsPrefix()
    val creators = listOfImages?.get(numberOfImage)?.data?.first()?.creators
    val keywords = listOfImages?.get(numberOfImage)?.data?.first()?.keywords?.first()
    val isFavourite = listOfImages?.get(numberOfImage)?.isFavourite ?: false

    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = contentColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box {
            SubcomposeAsyncImage(
                model = imageUrl,
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
                    .height(220.dp)
            )
            IconButton(
                onClick = {
                    val favourite = ImageToLoad(
                        title = title,
                        dateCreated = date,
                        link = imageUrl,
                        creators = creators,
                        keywords = null,
                        isFavourite = true
                    )

                    listOfImages?.get(numberOfImage)?.isFavourite = true

                    if (listOfImages != null) {
                        viewModel.onSaveFavourite(
                            favourite,
                            listOfImages,
                            numberOfImage
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(secondaryLight.copy(alpha = 0.75f), shape = CircleShape)
                    .border(
                        width = 1.dp,
                        color = primaryLight,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite Nasa Image",
                    tint = mainColor
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .background(surfaceDark),
        ) {
            Text(
                text = numberOfImage.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = contentColor
            )
        }
    }
}

