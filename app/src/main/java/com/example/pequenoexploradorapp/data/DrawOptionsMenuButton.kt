package com.example.pequenoexploradorapp.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Brush
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.theme.tertiaryLight

data class DrawOptionsMenuButton(
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
