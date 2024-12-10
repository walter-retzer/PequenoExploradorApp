package com.example.pequenoexploradorapp.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.theme.tertiaryLight


private class DrawOption(
    val titleButtonLeft: String,
    @DrawableRes
    val iconButtonLeft: Int,
    val actionButtonLeft: () -> Unit,
    val titleButtonRight: String,
    @DrawableRes
    val iconButtonRight: Int,
    val actionButtonRight: () -> Unit,
    val backgroundColor: Brush = Brush.linearGradient(
        colors = listOf(
            mainColor,
            tertiaryLight
        )
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuScreen(modifier: Modifier = Modifier) {

    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val options = remember {
        listOf(
            DrawOption(
                titleButtonLeft = "Pesquisar",
                iconButtonLeft = R.drawable.icon_search,
                actionButtonLeft = { },
                titleButtonRight = "Favoritos",
                iconButtonRight = R.drawable.icon_favorite,
                actionButtonRight = { }
            ),
            DrawOption(
                titleButtonLeft = "Rovers",
                iconButtonLeft = R.drawable.icon_rover,
                actionButtonLeft = { },
                titleButtonRight = "Planetas",
                iconButtonRight = R.drawable.icon_planet,
                actionButtonRight = { }
            ),
            DrawOption(
                titleButtonLeft = "Pesquisar",
                iconButtonLeft = R.drawable.icon_search,
                actionButtonLeft = { },
                titleButtonRight = "Favoritos",
                iconButtonRight = R.drawable.icon_favorite,
                actionButtonRight = { }
            ),
            DrawOption(
                titleButtonLeft = "Rovers",
                iconButtonLeft = R.drawable.icon_rover,
                actionButtonLeft = { },
                titleButtonRight = "Planetas",
                iconButtonRight = R.drawable.icon_planet,
                actionButtonRight = { }
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

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                text = option.titleButtonLeft, style = TextStyle(
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                )
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
                                text = option.titleButtonRight, style = TextStyle(
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                )
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
            }

//            Box {
//                AnimatedLottieFile(
//                    modifier = Modifier
//                        .size(200.dp)
//                        .align(Alignment.Center),
//                    file = R.raw.solar_system
//                )
//            }
        }
    }
}
