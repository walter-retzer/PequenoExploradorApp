package com.example.pequenoexploradorapp.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.pequenoexploradorapp.R


val QuickSand = FontFamily(
    Font(R.font.quicksandbold, FontWeight.Bold),
    Font(R.font.quicksandlight, FontWeight.Light),
    Font(R.font.quicksandmedium, FontWeight.Medium),
    Font(R.font.quicksandregular, FontWeight.Normal),
    Font(R.font.quicksandsemibold, FontWeight.SemiBold),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Bold,
    ),
    bodyMedium = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Medium,
    ),
    bodySmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
    ),
    labelSmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Light,
    )
)
