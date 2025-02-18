package com.example.pequenoexploradorapp.presentation.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.VerticalSpacer
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink80
import com.example.pequenoexploradorapp.presentation.viewmodel.QuestionsViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.QuestionsViewState
import org.koin.compose.koinInject


@Composable
fun QuestionsScreen(
    onNavigateToHome: () -> Unit,
    viewModel: QuestionsViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is QuestionsViewState.Init -> {
                viewModel.questions()
            }

            is QuestionsViewState.Loading -> {
                QuestionsUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = state.isLoading
                )
            }

            is QuestionsViewState.Success -> {
                QuestionsUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false,
                    isSuccess = true,
                    message = state.message.question1
                )
            }

            is QuestionsViewState.Error -> {
                QuestionsUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false,
                    hasMessage = true,
                    message = state.message
                )
            }
        }
    }
}


@Composable
fun QuestionsUI(
    snackBarHostState: SnackbarHostState,
    onNavigateToHome: () -> Unit,
    viewModel: QuestionsViewModel,
    paddingValues: PaddingValues,
    isLoading: Boolean,
    isSuccess: Boolean? = false,
    hasMessage: Boolean? = false,
    message: String? = null
) {
    val scope = rememberCoroutineScope()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    var initLoading by remember { mutableStateOf(false) }
    var snackBarIsActivated by remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpacer(16.dp)
            Text(
                text = "Seja Bem Vindo! $message",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                AnimatedLottieFile(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterVertically),
                    file = R.raw.astronaut_moon,
                    speed = 2f,
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                modifier = Modifier.align(alignment = Alignment.Start),
                text = "Informe seus dados:",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            VerticalSpacer(10.dp)

            if (isLoading || initLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.CenterHorizontally),
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
                snackBarIsActivated = true
            }
        }
//        if (isSuccess == true) {
//            LaunchedEffect(Unit) {
//                onNavigateToHome()
//            }
//        }
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
    }
}
