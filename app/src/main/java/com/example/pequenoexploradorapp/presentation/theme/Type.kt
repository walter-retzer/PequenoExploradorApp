package com.example.pequenoexploradorapp.presentation.theme

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
        fontSize = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Light,
        fontSize = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Light,
        fontSize = 8.sp
    )
)
