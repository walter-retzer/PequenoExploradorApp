package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.secure.SharedPrefApp
import com.example.pequenoexploradorapp.domain.secure.UserPreferences
import com.example.pequenoexploradorapp.presentation.components.AvatarImagesHorizontalPager
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.SimpleToolbar
import com.example.pequenoexploradorapp.presentation.components.VerticalSpacer
import com.example.pequenoexploradorapp.presentation.remember.rememberAvatars
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarSelectionScreen(
    onNavigateToProfile: () -> Unit,
    sharedPref: SharedPrefApp = koinInject()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val avatars = rememberAvatars()
    val pagerState = rememberPagerState(pageCount = { avatars.size })
    var image by remember { mutableIntStateOf(0) }


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            image = avatars[page]
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(toolbarBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            SimpleToolbar(
                title = "Avatar",
                onNavigationToBack = { onNavigateToProfile() },
                onNavigationClose = { onNavigateToProfile() }
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                VerticalSpacer(32.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        modifier = Modifier.size(64.dp),
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    VerticalSpacer(10.dp)
                    AvatarImagesHorizontalPager(
                       pagerState =  pagerState,
                        avatars = avatars
                    )
                    VerticalSpacer(10.dp)
                    IconButton(
                        modifier = Modifier.size(64.dp),
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                VerticalSpacer(20.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        text = "Escolha seu Avatar",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = ListItemDefaults.contentColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                    VerticalSpacer(12.dp)
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        text = "Nós possuimos uma lista com diversos avatares customizados",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = ListItemDefaults.contentColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            ProgressButton(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                text = "Selecionar",
                isLoading = false,
                onClick = {
                    sharedPref.saveInt(UserPreferences.IMAGE, image)
                    onNavigateToProfile()
                }
            )
        }
    }
}
