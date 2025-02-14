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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.RoverImageInfo
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedDate
import com.example.pequenoexploradorapp.domain.util.formattedHeadImageText
import com.example.pequenoexploradorapp.domain.util.toHttpsPrefix
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink40
import com.example.pequenoexploradorapp.presentation.theme.PurpleGrey80
import com.example.pequenoexploradorapp.presentation.theme.cardColor
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadRoverImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadRoverImageViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadRoverImageScreen(
    date: String,
    nameRover: String,
    onNavigateToHomeMenu: () -> Unit,
    viewModel: LoadRoverImageViewModel = koinInject()
) {
    val scrollState = rememberLazyGridState()
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
                title = nameRover,
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
            is LoadRoverImageViewState.Init -> {
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
                viewModel.onRequestRoverImages(date, nameRover)
            }

            is LoadRoverImageViewState.LoadingFavourite -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.updateListOfImageFavourite,
                    viewModel = viewModel,
                    isLoading = state.isLoading,
                )
            }

            is LoadRoverImageViewState.SuccessFavourite -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.updateListOfImageFavourite,
                    viewModel = viewModel,
                    isLoading = false,
                )
            }

            is LoadRoverImageViewState.Success -> {
                RenderSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    scope = scope,
                    listOfImagesFromApi = state.imagesFromApi,
                    viewModel = viewModel,
                    isLoading = false,
                )
            }

            is LoadRoverImageViewState.Error -> {
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
    listOfImagesFromApi: List<RoverImageInfo>,
    viewModel: LoadRoverImageViewModel,
    isLoading: Boolean,
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
        if (listOfImagesFromApi.isEmpty()) {
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
                    text = listOfImagesFromApi.formattedHeadImageText(),
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
                listOfImagesFromApi.let { imagesToLoad ->
                    items(imagesToLoad.size) { numberOfImage ->
                        LoadRoverImageOnCard(
                            listOfImages = imagesToLoad,
                            numberOfImage = numberOfImage,
                            viewModel = viewModel
                        )
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
                color = Pink40
            )
        }
    }
}


@Composable
fun LoadRoverImageOnCard(
    viewModel: LoadRoverImageViewModel,
    listOfImages: List<RoverImageInfo>,
    numberOfImage: Int
) {
    var isPressed by remember { mutableStateOf(false) }
    val imageToLoad = listOfImages[numberOfImage].imageUrl
    val date = listOfImages[numberOfImage].date.formattedDate()
    val isFavourite = listOfImages[numberOfImage].isFavourite
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
            listOfImages[numberOfImage].isFavourite = true
            isPressed = false
            val favourite = FavouriteImageToSave(
                id = System.currentTimeMillis(),
                title = null,
                dateCreated = date,
                link = imageToLoad,
                creators = null,
                keywords = null,
                isFavourite = true
            )
            viewModel.onSaveFavourite(
                favourite,
                listOfImages
            )
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
                model = imageToLoad.toHttpsPrefix(),
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
                textAlign = TextAlign.Center,
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
                    contentDescription = "Favorite Rover Image",
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
