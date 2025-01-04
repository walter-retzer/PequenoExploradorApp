package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoadNasaImageViewState
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadNasaImageScreen(
    imageSearch: String?,
    viewModel: LoadNasaImageViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    var progressButtonIsActivated by remember { mutableStateOf(false) }
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
            is LoadNasaImageViewState.DrawScreen -> {
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
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp)
                            .fillMaxWidth(),
                        text = imageSearch.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Justify,
                        color = Color.White
                    )
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .align(Alignment.Center)
                    )
                }
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
                    if (state.images.collection.metadata?.totalHits == 0) {
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
                                    .padding(16.dp),
                                text = "Foram encontradas ${state.images.collection.metadata?.totalHits} imagens",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Justify,
                                color = Color.White
                            )
                        }
                    }
                    Row {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.clipToBounds(),
                        ) {
                            state.images.collection.items?.size?.let { images ->
                                items(images) { numberOfImage ->
                                    LoadImageOnCard(
                                        images = state.images.collection.items,
                                        numberOfImage = numberOfImage
                                    )
                                }
                            }
                        }
                    }
                }
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
fun LoadImageOnCard(images: List<NasaImageItems>?, numberOfImage: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(
                    width = 1.dp,
                    color = contentColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { },
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images?.get(numberOfImage)?.links?.first()?.href)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.simple_background),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                text = "Imagem: ${numberOfImage + 1}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Justify,
                color = Color.White
            )
        }
    }
}
