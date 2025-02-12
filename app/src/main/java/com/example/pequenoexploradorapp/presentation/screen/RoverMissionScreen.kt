package com.example.pequenoexploradorapp.presentation.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.DrawOptionsMenuButton
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoverMissionScreen(
    onNavigateToRoverSpirit: (idName: String, image: Int) -> Unit,
    onNavigateToRoverCuriosity: (idName: String, image: Int) -> Unit,
    onNavigateToRoverOpportunity: (idName: String, image: Int) -> Unit,
    onNavigateToRoverPerseverance: (idName: String, image: Int) -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val options = remember {
        listOf(
            DrawOptionsMenuButton(
                titleButtonLeft = "Perseverance",
                iconButtonLeft = R.drawable.rover_perseverance,
                actionButtonLeft = { onNavigateToRoverPerseverance(BuildConfig.PERSEVERANCE, R.drawable.rover_perseverance) },
                titleButtonRight = "Curiosity",
                iconButtonRight = R.drawable.rover_curiosity,
                actionButtonRight = { onNavigateToRoverCuriosity(BuildConfig.CURIOSITY, R.drawable.rover_curiosity) },
            ),
            DrawOptionsMenuButton(
                titleButtonLeft = "Opportunity",
                iconButtonLeft = R.drawable.rover_opportunity,
                actionButtonLeft = { onNavigateToRoverOpportunity(BuildConfig.OPPORTUNITY, R.drawable.rover_opportunity) },
                titleButtonRight = "Spirit",
                iconButtonRight = R.drawable.rover_spirit,
                actionButtonRight = { onNavigateToRoverSpirit(BuildConfig.SPIRIT, R.drawable.rover_spirit) },
            )
        )
    }


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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    AnimatedLottieFile(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .size(200.dp)
                            .align(Alignment.TopCenter),
                        file = R.raw.rover
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 0.dp),
                        text = "Nossa Exploração pelo Planeta Marte começa agora",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    text = "Opções de rovers:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                    color = Color.White
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
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    textAlign = TextAlign.Center,
                                )
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Image(
                                painter = painterResource(option.iconButtonLeft),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, ListItemDefaults.contentColor, CircleShape)
                                    .background(ListItemDefaults.contentColor, CircleShape),
                                contentScale = ContentScale.Crop
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
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    textAlign = TextAlign.Center,
                                )
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Image(
                                painter = painterResource(option.iconButtonRight),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, ListItemDefaults.contentColor, CircleShape)
                                    .background(ListItemDefaults.contentColor, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}
