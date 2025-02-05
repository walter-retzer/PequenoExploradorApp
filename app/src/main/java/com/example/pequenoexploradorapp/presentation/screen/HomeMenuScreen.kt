package com.example.pequenoexploradorapp.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.DrawOptionsMenuButton
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuScreen(
    modifier: Modifier = Modifier,
    onNavigateToSearchImage: () -> Unit,
    onNavigateToPictureOfTheDay: () -> Unit,
    onNavigateToRoverMission: () -> Unit,
    onNavigateToFavouriteImage: () -> Unit,
    onNavigateToNasaVideos: () -> Unit,
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val options = remember {
        listOf(
            DrawOptionsMenuButton(
                titleButtonLeft = "Imagens",
                iconButtonLeft = R.drawable.icon_search,
                actionButtonLeft = { onNavigateToSearchImage() },
                titleButtonRight = "Videos",
                iconButtonRight = R.drawable.icon_play_videos,
                actionButtonRight = { onNavigateToNasaVideos() }
            ),
            DrawOptionsMenuButton(
                titleButtonLeft = "Rovers",
                iconButtonLeft = R.drawable.icon_space_rover,
                actionButtonLeft = { onNavigateToRoverMission() },
                titleButtonRight = "Planetas",
                iconButtonRight = R.drawable.icon_planet_earth,
                actionButtonRight = { onNavigateToSearchImage() }
            ),
            DrawOptionsMenuButton(
                titleButtonLeft = "Curiosidades",
                iconButtonLeft = R.drawable.icon_question,
                actionButtonLeft = { onNavigateToSearchImage() },
                titleButtonRight = "Perfil",
                iconButtonRight = R.drawable.icon_astronaut,
                actionButtonRight = { onNavigateToSearchImage() }
            ),
            DrawOptionsMenuButton(
                titleButtonLeft = "Sistema\nSolar",
                iconButtonLeft = R.drawable.icon_solar_system,
                actionButtonLeft = { onNavigateToSearchImage() },
                titleButtonRight = "Destaque\ndo Dia",
                iconButtonRight = R.drawable.icon_comet,
                actionButtonRight = { onNavigateToPictureOfTheDay() }
            ),
            DrawOptionsMenuButton(
                titleButtonLeft = "Favoritos",
                iconButtonLeft = R.drawable.icon_favorite,
                actionButtonLeft = { onNavigateToFavouriteImage() },
                titleButtonRight = "Galeria",
                iconButtonRight = R.drawable.icon_gallery,
                actionButtonRight = { onNavigateToPictureOfTheDay() }
            )
        )
    }

    BackHandler { }

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
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                )
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .fillMaxWidth()
                            .height(250.dp)
                            .align(Alignment.TopCenter),
                        file = R.raw.astronaut_exploration,
                        contentScale = ContentScale.FillWidth
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        text = "Nossa Exploração pelo Universo começa agora",
                        color = Color.White,
                        fontSize = 21.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    text = "Opções de exploração:",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.labelSmall,
                )
                options.forEach { option ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(15))
                                .clickable { option.actionButtonLeft() }
                                .background(option.backgroundColor)
                                .width(150.dp)
                                .heightIn(150.dp)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = option.titleButtonLeft,
                                color = Color.White,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Image(
                                painter = painterResource(option.iconButtonLeft),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                            )
                        }
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(15))
                                .clickable { option.actionButtonRight() }
                                .background(option.backgroundColor)
                                .width(150.dp)
                                .heightIn(150.dp)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = option.titleButtonRight,
                                color = Color.White,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Image(
                                painter = painterResource(option.iconButtonRight),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
