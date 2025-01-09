package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
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
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.RoverMissionDetailViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.RoverMissionDetailViewState
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoverMissionDetailScreen(
    idName: String?,
    viewModel: RoverMissionDetailViewModel = koinInject()
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
                title = "Rovers",
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
            is RoverMissionDetailViewState.Error -> {
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

            is RoverMissionDetailViewState.Init -> {
                viewModel.onRoverMissionDetailRequest(idName)
            }

            is RoverMissionDetailViewState.Loading -> {
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

            is RoverMissionDetailViewState.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .paint(
                            painterResource(id = R.drawable.simple_background),
                            contentScale = ContentScale.FillBounds
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black)
                                .border(
                                    width = 2.dp,
                                    color = ListItemDefaults.contentColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { },
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.rover_spirit),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.dp,
                                        ListItemDefaults.contentColor,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(Color.Black, RoundedCornerShape(16.dp))
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Nome: ${state.mission.rover.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Status: Missão ${state.mission.rover.status}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Total de Fotos: ${state.mission.rover.totalPhotos}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Sol: ${state.mission.rover.maxSun}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Lançamento: ${state.mission.rover.launchDate}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Pouso em Marte: ${state.mission.rover.landingDate}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Última data em operação: ${state.mission.rover.maxDate}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Quantidade de cameras: ${state.mission.rover.cameras.size - 1}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor
                            )
                            ProgressButton(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                text = "Pesquisar",
                                isLoading = false,
                                onClick = {

                                }
                            )
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
}
