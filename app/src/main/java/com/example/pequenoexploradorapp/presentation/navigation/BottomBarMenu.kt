package com.example.pequenoexploradorapp.presentation.navigation

import com.example.pequenoexploradorapp.R


object NavTitle {
    const val HOME = "Menu"
    const val IMAGES = "Imagens"
    const val FAVOURITE = "Favoritos"
}

open class Item(
    val pathRoute: String,
    val title: String,
    val icon: Int,
)

sealed class NavItem {
    object Home :
        Item(
            pathRoute = Route.HomeScreenRoute.route,
            title = NavTitle.HOME,
            icon = R.drawable.icon_home
        )
    object IMAGES :
        Item(
            pathRoute = Route.SearchImageScreenRoute.route,
            title = NavTitle.IMAGES,
            icon = R.drawable.icon_search
        )
    object FAVOURITE :
        Item(
            pathRoute = Route.LoadFavouriteImageScreenRoute.route,
            title = NavTitle.FAVOURITE,
            icon = R.drawable.icon_favorite
        )
}
