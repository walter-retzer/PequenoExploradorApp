package com.example.pequenoexploradorapp.presentation.remember

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.pequenoexploradorapp.R


@Composable
fun rememberAvatars(): List<Int> = remember { Avatar.avatars }

object Avatar {
    val avatars = listOf(
        R.drawable.perfil01,
        R.drawable.perfil02,
        R.drawable.perfil03,
        R.drawable.perfil04,
        R.drawable.perfil05,
        R.drawable.perfil06,
        R.drawable.perfil07,
        R.drawable.perfil08,
        R.drawable.perfil09,
        R.drawable.perfil10,
        R.drawable.perfil11,
        R.drawable.perfil12,
        R.drawable.perfil13,
        R.drawable.perfil14,
        R.drawable.perfil16,
        R.drawable.perfil17,
        R.drawable.perfil18,
        R.drawable.perfil19,
        R.drawable.perfil20
    )
}