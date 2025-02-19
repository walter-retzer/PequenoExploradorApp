package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.enableButton
import com.example.pequenoexploradorapp.domain.util.formattedDate
import com.example.pequenoexploradorapp.domain.util.formattedMission
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink80
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.theme.scaffoldColor
import com.example.pequenoexploradorapp.presentation.viewmodel.RoverMissionDetailViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.RoverMissionDetailViewState
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoverMissionDetailScreen(
    roverName: String,
    image: Int,
    onNavigateToSearchImage: (firstDate: String, lastDate: String, nameRover: String) -> Unit,
    onNavigateToHomeMenu: () -> Unit,
    viewModel: RoverMissionDetailViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val enableButton = roverName.enableButton()
    var snackBarIsActivated by remember { mutableStateOf(false) }


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
                title = roverName,
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
            is RoverMissionDetailViewState.Init -> {
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
                        color = Pink80
                    )
                }
                viewModel.onRoverMissionDetailRequest(roverName)
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
                                .border(
                                    width = 1.dp,
                                    color = ListItemDefaults.contentColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { },
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors =  CardDefaults.cardColors().copy(
                                containerColor = scaffoldColor
                            )
                        ) {
                            Image(
                                painter = painterResource(image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(310.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 16.dp),
                                text = "Nome: ${state.mission.rover.name}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Status: ${state.mission.rover.status.formattedMission()}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Total de Fotos: ${state.mission.rover.totalPhotos}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Sol: ${state.mission.rover.maxSun}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Lançamento: ${state.mission.rover.launchDate.formattedDate()}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Pouso em Marte: ${state.mission.rover.landingDate.formattedDate()}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Última atividade: ${state.mission.rover.maxDate.formattedDate()}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                text = "Quantidade de cameras: ${state.mission.rover.cameras.size - 1}",
                                textAlign = TextAlign.Start,
                                color = ListItemDefaults.contentColor,
                                style = MaterialTheme.typography.titleLarge
                            )
                            if(enableButton) {
                                ProgressButton(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    text = "Pesquisar",
                                    isLoading = false,
                                    onClick = {
                                        onNavigateToSearchImage(
                                            state.mission.rover.landingDate.formattedDate(),
                                            state.mission.rover.maxDate.formattedDate(),
                                            roverName
                                        )
                                    }
                                )
                            }
                            else Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            is RoverMissionDetailViewState.Error -> {
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
                            onNavigateToHomeMenu()
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
}
