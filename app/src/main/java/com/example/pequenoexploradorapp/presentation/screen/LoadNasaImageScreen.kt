package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.viewmodel.SearchImageViewModel
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadNasaImageScreen(
    imageSearch: String?,
    viewModel: SearchImageViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val textSearchImage by viewModel.searchImageState.collectAsState()
    val isVisible by remember { derivedStateOf { textSearchImage.textInput.isNotBlank() } }
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                )
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
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
        }


    }
}