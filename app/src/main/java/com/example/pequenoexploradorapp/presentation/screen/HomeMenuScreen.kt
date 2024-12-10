package com.example.pequenoexploradorapp.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.theme.inverseOnSurfaceDark
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.theme.outlineVariantDark
import com.example.pequenoexploradorapp.presentation.theme.tertiaryLight


private class DrawOption(
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Brush = Brush.linearGradient(
        colors = listOf(
            mainColor,
            tertiaryLight
        )
    ),
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuScreen(modifier: Modifier = Modifier) {

    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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

        val options = remember {
            listOf(
                DrawOption(
                    title = "Pesquisar",
                    icon = Icons.Filled.Search,
                    action = { }
                ),
                DrawOption(
                    title = "Favoritos",
                    icon = Icons.Filled.Favorite,
                    action = { }
                ),
            )
        }

//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .paint(
//                    painterResource(id = R.drawable.simple_background),
//                    contentScale = ContentScale.FillBounds
//                ),
//        ) {
//            item { options ->
//
//            }
//        }


        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                ),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEach { option ->
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(15))
                                .clickable { option.action() }
                                .background(option.backgroundColor)
                                .width(150.dp)
                                .heightIn(150.dp)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = option.title, style = TextStyle(
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                )
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Icon(
                                option.icon, contentDescription = null,
                                Modifier.size(64.dp),
                                tint = Color.White
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEach { option ->
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(15))
                                .clickable { option.action() }
                                .background(option.backgroundColor)
                                .width(150.dp)
                                .heightIn(150.dp)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = option.title, style = TextStyle(
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                )
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Icon(
                                option.icon, contentDescription = null,
                                Modifier.size(64.dp),
                                tint = Color.White
                            )
                        }
                    }

                }


                Text(
                    text = "Menu: ",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(R.drawable.splash),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.animation_telescopy
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.astronaut_animation
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.astronaut_blue
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.astronaut_curiosity
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.astronaut_exploration
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.astronaut_illustration
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.astronaut_moon
                    )
                }
                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.astronaut_rocket
                    )
                }
                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.funny_rover
                    )
                }
                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.heart_fav
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.looking_at_stars
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.planets
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.radar_searching
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.rover
                    )
                }

                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        file = R.raw.solar_system
                    )
                }
            }
        }
    }
}
