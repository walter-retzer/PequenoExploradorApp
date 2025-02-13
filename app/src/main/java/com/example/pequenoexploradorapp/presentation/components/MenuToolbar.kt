package com.example.pequenoexploradorapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.presentation.theme.navColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuToolbar(
    color: Color = navColor,
    titleColor: Color = Color.White.copy(0.75f),
    title: String,
    onNavigationToMenu: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigationToProfile: () -> Unit,
    toolbarBehavior: TopAppBarScrollBehavior? = null,
    isActivatedBadge: Boolean = false
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = color,
            scrolledContainerColor = color,
            titleContentColor = titleColor,
        ),
        title = {
            Text(
                text = title,
                color = titleColor,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationToMenu() }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description",
                    tint = titleColor
                )
            }
        },
        actions = {
            BadgedBox(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .clickable { onNavigateToNotifications() },
                badge = {
                    if (isActivatedBadge) {
                        Badge(
                            modifier = Modifier.padding(start = 3.dp, bottom = 10.dp)
                        ) {
                            Text(text = "1")
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notification",
                    tint = titleColor
                )
            }
            IconButton(onClick = { onNavigationToProfile() }) {
                Icon(
                    imageVector = Icons.Default.PermIdentity,
                    contentDescription = "Profile",
                    tint = titleColor
                )
            }
        },
        scrollBehavior = toolbarBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleToolbar(
    color: Color = navColor,
    titleColor: Color = Color.White.copy(0.75f),
    title: String,
    onNavigationToBack: () -> Unit,
    onNavigationClose: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = color,
            scrolledContainerColor = color,
            titleContentColor = titleColor,
        ),
        title = {
            Text(
                text = title,
                color = titleColor,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationToBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Icon ArrowBack",
                    tint = titleColor
                )
            }
        },
        actions = {
            IconButton(onClick = { onNavigationClose() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Icon Close",
                    tint = titleColor
                )
            }
        }
    )
}
