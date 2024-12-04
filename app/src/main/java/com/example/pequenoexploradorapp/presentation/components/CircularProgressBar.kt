package com.example.pequenoexploradorapp.presentation.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pequenoexploradorapp.presentation.theme.mainColor

@Composable
fun MyAppCircularProgressIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = mainColor
    )
}