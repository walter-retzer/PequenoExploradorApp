package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.Planet
import com.example.pequenoexploradorapp.data.planets
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetsScreen(
    onNavigateToLoadNasaImage: (imageSearch: String?) -> Unit,
) {
    val stateScroll = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val listOfPlanets = planets


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            MenuToolbar(
                title = "Planetas",
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
                .fillMaxWidth()
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_sun),
                modifier = Modifier
                    .fillMaxWidth(),
                contentDescription = "Pager Image",
                contentScale = ContentScale.FillWidth,
            )
            LazyColumn {
                items(listOfPlanets) { planet ->
                    PlanetItem(planet)
                }
            }
        }
    }
}


@Composable
fun PlanetItem(planet: Planet) {
    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(600.dp, 120.dp)) {
                    drawArc(
                        color = Color.LightGray,
                        topLeft = Offset(0f, -size.height / 2),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(5f),
                        size = Size(size.width, size.height)
                    )
                }
                Image(
                    painter = painterResource(id = planet.img),
                    contentDescription = "Imagem",
                    modifier = Modifier
                        .size(150.dp)
                        .clipToBounds(),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                text = planet.name,
                color = Color.White,
                fontSize = 21.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    Spacer(Modifier.height(32.dp))
}
