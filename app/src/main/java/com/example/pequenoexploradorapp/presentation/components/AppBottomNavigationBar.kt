package com.example.pequenoexploradorapp.presentation.components

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.example.pequenoexploradorapp.presentation.navigation.Item


@Composable
fun AppBottomNavigationBar(
    navItems: List<Item>,
    navController: NavHostController
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination?.route

    BottomAppBar(
        actions = {
            navItems.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination == item.pathRoute,
                    onClick = {
                        navController.navigate(item.pathRoute,
                            navOptions {
                                launchSingleTop = true
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) { saveState = true }
                                }
                            }
                        )
                    },
                    icon = {
                        Icon(painter = painterResource(id = item.icon), contentDescription = item.title)
                    },
                    label = {
                        Text(text = item.title)
                    }
                )
            }
        },
        containerColor = Color.Black,
        contentColor = Color.Black
    )
}
