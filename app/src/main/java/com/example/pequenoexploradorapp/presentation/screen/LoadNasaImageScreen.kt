package com.example.pequenoexploradorapp.presentation.screen

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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
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
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedDate
import com.example.pequenoexploradorapp.domain.util.toHttpsPrefix
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink40
import com.example.pequenoexploradorapp.presentation.theme.PurpleGrey80
import com.example.pequenoexploradorapp.presentation.theme.cardColor
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadNasaImageScreen(
    imageSearch: String?,
    onNavigateToSearchImage: () -> Unit,
    viewModel: LoadNasaImageViewModel = koinInject()
) {
    var snackBarIsActivated by remember { mutableStateOf(false) }
    val scrollState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()


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
                        color = Pink40
                    )
                }
                viewModel.onNasaImageSearch(imageSearch)
            }

            is LoadNasaImageViewState.Loading -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.listOfNasaImage,
                    viewModel = viewModel,
                    isLoading = state.isLoading,
                    isLoadingNextItems = false,
                    totalHits = state.totalHits
                )
            }

            is LoadNasaImageViewState.Success -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.images,
                    viewModel = viewModel,
                    isLoading = false,
                    isLoadingNextItems = true,
                    totalHits = state.totalHits
                )
            }

            is LoadNasaImageViewState.SuccessAddFavourite -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.updateListOfImageFavourite,
                    viewModel = viewModel,
                    isLoading = false,
                    isLoadingNextItems = true,
                    totalHits = state.totalHits
                )
            }

            is LoadNasaImageViewState.Error -> {
                snackBarIsActivated = state.isActivated
                if(snackBarIsActivated) {
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
fun RenderSuccess(
    paddingValues: PaddingValues,
    scrollState: LazyGridState,
    scope: CoroutineScope,
    listOfImagesFromApi: List<NasaImageItems>,
    viewModel: LoadNasaImageViewModel,
    isLoading: Boolean,
    isLoadingNextItems: Boolean,
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
                    textAlign = TextAlign.Justify,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
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
                    textAlign = TextAlign.Justify,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
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
                items(listOfImagesFromApi.size) { numberOfImage ->
                    LoadImageOnCard(
                        listOfImages = listOfImagesFromApi,
                        numberOfImage = numberOfImage,
                        viewModel = viewModel
                    )
                }
            }
        }
        // Handle infinite scrolling
        InfiniteListHandler(
            listState = scrollState,
            isLoadingNextItems = isLoadingNextItems,
            listOfImagesFromApi = listOfImagesFromApi,
            totalHits = totalHits,
            onLoadMore = {
                viewModel.loadNextImage()
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
                color = Pink40
            )
        }
    }
}

@Composable
fun InfiniteListHandler(
    listState: LazyGridState,
    isLoadingNextItems: Boolean,
    listOfImagesFromApi: List<NasaImageItems>,
    totalHits: Int,
    buffer: Int = 20,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (totalItemsCount - buffer) && isLoadingNextItems &&
            totalHits != listOfImagesFromApi.size
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
    var isPressed by remember { mutableStateOf(false) }
    val title = listOfImages?.get(numberOfImage)?.data?.first()?.title
    val date = listOfImages?.get(numberOfImage)?.data?.first()?.dateCreated?.formattedDate() ?: ""
    val imageUrl = listOfImages?.get(numberOfImage)?.links?.first()?.href?.toHttpsPrefix()
    val creators = listOfImages?.get(numberOfImage)?.data?.first()?.creators
    val keywords = listOfImages?.get(numberOfImage)?.data?.first()?.keywords?.first()
    val isFavourite = listOfImages?.get(numberOfImage)?.isFavourite ?: false
    val index = numberOfImage + 1
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isFavourite) 1.5f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = ""
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(600L)
            listOfImages?.get(numberOfImage)?.isFavourite = true
            isPressed = false
            val favourite = FavouriteImageToSave(
                id = System.currentTimeMillis(),
                title = title,
                dateCreated = date,
                link = imageUrl,
                creators = creators,
                keywords = null,
                isFavourite = true
            )
            if (listOfImages != null) {
                viewModel.onSaveFavourite(
                    favourite,
                    listOfImages
                )
            }
        }
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Color.Gray,
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
                            color = Pink40
                        )
                    }
                },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Text(
                text = index.toString(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp)
                    .wrapContentSize(),
                textAlign = TextAlign.End,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
            IconButton(
                onClick = { isPressed = true },
                interactionSource = interactionSource,
                modifier = Modifier
                    .scale(scale)
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(mainColor.copy(alpha = 0.5f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite Nasa Image",
                    tint = Color.White
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .background(cardColor),
        ) {
            Text(
                text = date,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                color = PurpleGrey80,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
