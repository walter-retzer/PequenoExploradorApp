package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.derivedStateOf
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
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.parallax.ParallaxView
import com.example.pequenoexploradorapp.presentation.components.parallax.model.ContainerSettings
import com.example.pequenoexploradorapp.presentation.components.parallax.model.ParallaxOrientation
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.backgroundColor
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.SearchNasaViewModel
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchNasaVideoScreen(
    onNavigateToLoadNasaVideo: (imageSearch: String?) -> Unit,
    viewModel: SearchNasaViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val stateScroll = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val textSearchImage by viewModel.searchImageState.collectAsState()
    val isVisible by remember { derivedStateOf { textSearchImage.textInput.isNotBlank() } }
    var snackBarIsActivated by remember { mutableStateOf(false) }
    var isScrollActive by remember { mutableStateOf(false) }


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
                title = "Videos",
                onNavigationToMenu = { },
                onNavigationToProfile = { },
                onNavigateToNotifications = { },
                toolbarBehavior = toolbarBehavior,
                isActivatedBadge = false
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                )
                .fillMaxWidth()
                .clickable { isScrollActive = true }
                .verticalScroll(stateScroll),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ParallaxView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(470.dp)
                    .background(Color.Black),
                backgroundContent = {
                    Image(
                        painter = painterResource(id = R.drawable.bg_galaxy),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                },
                middleContent = { },
                foregroundContent = {
                    Image(
                        painter = painterResource(id = R.drawable.fg_astronaut_night),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                backgroundContainerSettings = ContainerSettings(
                    scale = 1.5f,
                ),
                foregroundContainerSettings = ContainerSettings(
                    scale = 1.5f,
                    alignment = Alignment.BottomCenter
                ),
                verticalOffsetLimit = 0.15f,
                horizontalOffsetLimit = 0.5f,
                movementIntensityMultiplier = 40,
                orientation = ParallaxOrientation.Full
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                text = "Maravilhe-se com os videos mais fascinantes do Universo",
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(20.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = backgroundColor
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(20.dp),
                value = textSearchImage.textInput,
                isError = false,
                placeholder = {
                    Text(
                        "Procurar Videos",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = { viewModel.onTextInputChange(it) },
                trailingIcon = {
                    if (isVisible) {
                        IconButton(
                            onClick = { viewModel.onTextInputChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                leadingIcon = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            )
            ProgressButton(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                text = "Pesquisar",
                onClick = {
                    onNavigateToLoadNasaVideo(textSearchImage.textInput.trimEnd())
                },
                enable = textSearchImage.textInput.isNotEmpty()
            )
            LaunchedEffect(isScrollActive) {
                stateScroll.animateScrollTo(1000)
                isScrollActive = false
            }
        }
    }
}
