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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.ImageDecoderDecoder
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.FirebaseDataBaseResponse
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.VerticalSpacer
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink80
import com.example.pequenoexploradorapp.presentation.theme.backgroundColor
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.QuestionsViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.QuestionsViewState
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(
    onNavigateToHome: () -> Unit,
    viewModel: QuestionsViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


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
                title = "Curiosidades",
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
            is QuestionsViewState.Init -> {
                viewModel.getQuestions()
            }

            is QuestionsViewState.Loading -> {
                QuestionsSuccessUI(
                    snackBarHostState = snackBarHostState,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = state.isLoading
                )
            }

            is QuestionsViewState.Success -> {
                QuestionsSuccessUI(
                    snackBarHostState = snackBarHostState,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false,
                    response = state.response
                )
            }

            is QuestionsViewState.Error -> {
                QuestionsErrorUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    paddingValues = paddingValues,
                    hasMessage = true,
                    message = state.message
                )
            }
        }
    }
}


@Composable
fun QuestionsErrorUI(
    snackBarHostState: SnackbarHostState,
    onNavigateToHome: () -> Unit,
    paddingValues: PaddingValues,
    hasMessage: Boolean? = false,
    message: String? = null
) {
    val scope = rememberCoroutineScope()
    var snackBarIsActivated by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .paint(
                painterResource(id = R.drawable.simple_background),
                contentScale = ContentScale.FillBounds
            )
    )
    if (hasMessage == true && !snackBarIsActivated) {
        LaunchedEffect(Unit) {
            snackBarOnlyMessage(
                snackBarHostState = snackBarHostState,
                coroutineScope = scope,
                message = message.toString(),
                duration = SnackbarDuration.Long
            )
            snackBarIsActivated = true
        }
    }
    if (hasMessage == true) {
        LaunchedEffect(Unit) {
            delay(3000L)
            onNavigateToHome()
        }
    }
}

@Composable
fun QuestionsSuccessUI(
    snackBarHostState: SnackbarHostState,
    viewModel: QuestionsViewModel,
    paddingValues: PaddingValues,
    isLoading: Boolean,
    response: FirebaseDataBaseResponse? = null,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components { add(ImageDecoderDecoder.Factory()) }
        .build()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    var snackBarIsActivated by remember { mutableStateOf(false) }
    var question01IsActivated by remember { mutableStateOf(false) }
    var question02IsActivated by remember { mutableStateOf(false) }
    var question03IsActivated by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .paint(
                painterResource(id = R.drawable.simple_background),
                contentScale = ContentScale.FillBounds
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isConnected == false && !snackBarIsActivated) {
            LaunchedEffect(Unit) {
                snackBarOnlyMessage(
                    snackBarHostState = snackBarHostState,
                    coroutineScope = scope,
                    message = ConstantsApp.ERROR_WITHOUT_INTERNET,
                    duration = SnackbarDuration.Long
                )
                snackBarIsActivated = true
            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center),
                color = Pink80
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    text = "Venha descubrir as curiosidades sobre o Universo",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Row {
                    AnimatedLottieFile(
                        modifier = Modifier.fillMaxWidth(),
                        file = R.raw.astronaut_curiosity,
                        speed = 1f,
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    modifier = Modifier.align(alignment = Alignment.Start),
                    text = "Temos 03 perguntas para testar o seu conhecimento:",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                        .background(backgroundColor, RoundedCornerShape(20.dp)),
                    text = "Pergunta\nIntergaláctica 1",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    text = response?.question1.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Box {
                    val question = response?.imageQuestionDefault
                    val image = response?.imageQuestion1
                    SubcomposeAsyncImage(
                        imageLoader = imageLoader,
                        model = if (question01IsActivated) image else question,
                        loading = {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                    color = Pink80
                                )
                            }
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                question01IsActivated = !question01IsActivated
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                            .background(Color.Black, RoundedCornerShape(20.dp)),
                    )
                }
                AnimatedVisibility(
                    visible = question01IsActivated,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        text = response?.answer1.toString(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                        .background(backgroundColor, RoundedCornerShape(20.dp)),
                    text = "Pergunta\nIntergaláctica 2",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    text = response?.question2.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Box {
                    val question = response?.imageQuestionDefault
                    val image = response?.imageQuestion2
                    SubcomposeAsyncImage(
                        imageLoader = imageLoader,
                        model = if (question02IsActivated) image else question,
                        loading = {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                    color = Pink80
                                )
                            }
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                question02IsActivated = !question02IsActivated
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                            .background(Color.Black, RoundedCornerShape(20.dp)),
                    )
                }
                AnimatedVisibility(
                    visible = question02IsActivated,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        text = response?.answer2.toString(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                        .background(backgroundColor, RoundedCornerShape(20.dp)),
                    text = "Pergunta\nIntergaláctica 3",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    text = response?.question3.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Box {
                    val question = response?.imageQuestionDefault
                    val image = response?.imageQuestion3
                    SubcomposeAsyncImage(
                        imageLoader = imageLoader,
                        model = if (question03IsActivated) image else question,
                        loading = {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center),
                                    color = Pink80
                                )
                            }
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                question03IsActivated = !question03IsActivated
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                            .background(Color.Black, RoundedCornerShape(20.dp)),
                    )
                }
                AnimatedVisibility(
                    visible = question03IsActivated,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        text = response?.answer3.toString(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                VerticalSpacer(10.dp)
            }
        }
    }
}
