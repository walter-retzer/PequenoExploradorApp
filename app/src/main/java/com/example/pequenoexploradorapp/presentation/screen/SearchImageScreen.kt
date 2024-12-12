package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.parallax.ParallaxView
import com.example.pequenoexploradorapp.presentation.components.parallax.model.ContainerSettings
import com.example.pequenoexploradorapp.presentation.viewmodel.SearchImageViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SearchImageViewState
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchImageScreen(
    viewModel: SearchImageViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val textSearchImage by viewModel.searchImageState.collectAsState()
    var progressButtonIsActivated by remember { mutableStateOf(false) }
    var snackBarIsActivated by remember { mutableStateOf(false) }

    ParallaxView(
        backgroundContent = {
            Image(
                painter = painterResource(id = R.drawable.bg_galaxy),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillHeight
            )
        },
        middleContent = {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                topBar = {
                    MenuToolbar(
                        title = "Menu",
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
                    is SearchImageViewState.DrawScreen -> {}
                    is SearchImageViewState.Loading -> {
                        progressButtonIsActivated = true
                    }
                    is SearchImageViewState.Error -> {
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
                    is SearchImageViewState.Success -> {
                        progressButtonIsActivated = false
                        snackBarIsActivated = true
                        LaunchedEffect(snackBarIsActivated) {
                            snackBarOnlyMessage(
                                snackBarHostState = snackBarHostState,
                                coroutineScope = scope,
                                message = ConstantsApp.SUCCESS_SIGN_IN,
                                duration = SnackbarDuration.Long
                            )
                            snackBarIsActivated = false
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
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
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Encontre as imagens mais facinantes do Universo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Justify,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.DarkGray, RoundedCornerShape(20.dp)),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrectEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(20.dp),
                            value = textSearchImage.textInput,
                            isError = false,
                            placeholder = { Text("Procurar Imagens") },
                            onValueChange = { viewModel.onTextInputChange(it) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ProgressButton(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            text = "Pesquisar",
                            isLoading = progressButtonIsActivated,
                            onClick = { viewModel.onNasaImageSearch(textSearchImage.textInput) }
                        )
                    }
                }
            }
        },
        foregroundContent = {
            Image(
                painter = painterResource(id = R.drawable.fg_astronaut_night),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        },
        backgroundContainerSettings = ContainerSettings(
            scale = 1.2f,
        ),
        foregroundContainerSettings = ContainerSettings(
            scale = 1.5f,
            alignment = Alignment.BottomCenter
        ),
        verticalOffsetLimit = 0.12f,
        horizontalOffsetLimit = 0.5f,
        movementIntensityMultiplier = 40,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    )
}
