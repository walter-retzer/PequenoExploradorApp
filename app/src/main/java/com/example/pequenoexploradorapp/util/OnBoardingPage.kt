package com.example.pequenoexploradorapp.util

import androidx.annotation.DrawableRes
import com.example.pequenoexploradorapp.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    object First : OnBoardingPage(
        image = R.drawable.splah_screen_image,
        title = "Pequeno Explorador",
        description = "Seja um Astronauta Explorador!"
    )

    object Second : OnBoardingPage(
        image = R.drawable.splah_screen_image,
        title = "Pequeno Explorador",
        description = "Muitas curiosidades e fotos do Universo"
    )

    object Third : OnBoardingPage(
        image = R.drawable.splah_screen_image,
        title = "Pequeno Explorador",
        description = "Conheça os Planetas do Sistema Solar"
    )
}
