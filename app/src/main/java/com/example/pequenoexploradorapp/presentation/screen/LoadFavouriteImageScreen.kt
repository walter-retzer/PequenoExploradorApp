package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedHeadText
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarWithActionButton
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.theme.primaryLight
import com.example.pequenoexploradorapp.presentation.theme.secondaryLight
import com.example.pequenoexploradorapp.presentation.theme.surfaceDark
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadFavouriteImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadFavouriteImageViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadFavouriteImageScreen(
    viewModel: LoadFavouriteImageViewModel = koinInject(),
    onNavigateToHomeMenu: () -> Unit
) {
    val scrollState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val listOfFavouriteImage by viewModel.listOfFavoriteImage.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    var progressButtonIsActivated by remember { mutableStateOf(false) }
    var snackBarIsActivated by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            MenuToolbar(
                title = "Favoritos",
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
            is LoadFavouriteImageViewState.Init -> {
                viewModel.onGetFavouriteImageList()
            }

            is LoadFavouriteImageViewState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .paint(
                            painterResource(id = R.drawable.simple_background),
                            contentScale = ContentScale.FillBounds
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        text = "Carregando suas imagens favoritas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    AnimatedLottieFile(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .size(350.dp)
                            .align(Alignment.Center),
                        file = R.raw.heart_fav,
                        speed = 2f
                    )
                }
            }

            is LoadFavouriteImageViewState.Error -> {
                RenderImageFavouriteSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    listOfImagesFromDb = listOfFavouriteImage,
                    viewModel = viewModel,
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    isLoading = false,
                )

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
                    delay(5000L)
                    onNavigateToHomeMenu()
                }
            }

            is LoadFavouriteImageViewState.Success -> {
                RenderImageFavouriteSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    listOfImagesFromDb = state.images,
                    viewModel = viewModel,
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    isLoading = false,
                )
            }

            is LoadFavouriteImageViewState.LoadingRemoveFavourite -> {
                RenderImageFavouriteSuccess(
                    paddingValues = paddingValues,
                    scrollState = scrollState,
                    listOfImagesFromDb = listOfFavouriteImage,
                    viewModel = viewModel,
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    isLoading = state.isLoading,
                )
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
}


@Composable
fun RenderImageFavouriteSuccess(
    paddingValues: PaddingValues,
    scrollState: LazyGridState,
    listOfImagesFromDb: List<FavouriteImageToSave>,
    viewModel: LoadFavouriteImageViewModel,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
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
        Row {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = listOfImagesFromDb.formattedHeadText(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Justify,
                color = Color.White
            )
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
                listOfImagesFromDb.let { imagesToLoad ->
                    items(imagesToLoad.size) { numberOfImage ->
                        LoadFavouriteImageOnCard(
                            listOfImages = imagesToLoad,
                            numberOfImage = numberOfImage,
                            viewModel = viewModel,
                            scope = scope,
                            snackBarHostState = snackBarHostState
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
                color = mainColor
            )
        }
    }
}

@Composable
fun LoadFavouriteImageOnCard(
    listOfImages: List<FavouriteImageToSave>?,
    numberOfImage: Int,
    viewModel: LoadFavouriteImageViewModel,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) {
    val id = listOfImages?.get(numberOfImage)?.id ?: System.currentTimeMillis()
    val title = listOfImages?.get(numberOfImage)?.title
    val date = listOfImages?.get(numberOfImage)?.dateCreated ?: ""
    val imageUrl = listOfImages?.get(numberOfImage)?.link
    val creators = listOfImages?.get(numberOfImage)?.creators
    val keywords = listOfImages?.get(numberOfImage)?.keywords?.first()
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
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(secondaryLight.copy(alpha = 0.75f), shape = CircleShape)
                    .border(
                        width = 1.dp,
                        color = primaryLight,
                        shape = CircleShape
                    ),
                onClick = {
                    snackBarWithActionButton(
                        coroutineScope = scope,
                        snackBarHostState = snackBarHostState,
                        actionLabel = ConstantsApp.DELETE_FAVOURITE_IMAGE_YES,
                        message = ConstantsApp.DELETE_FAVOURITE_IMAGE,
                        onAction = {
                            val favourite = FavouriteImageToSave(
                                id = id,
                                title = title,
                                dateCreated = date,
                                link = imageUrl,
                                creators = creators,
                                keywords = null,
                                isFavourite = isFavourite
                            )
                            viewModel.onRemoveFavouriteImage(favourite)
                        },
                        onDismiss = { }
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
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
                text = date,
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
