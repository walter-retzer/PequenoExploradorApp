package com.example.pequenoexploradorapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pequenoexploradorapp.R

@Composable
fun AvatarImagesHorizontalPager() {
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
    val avatarSize = 180.dp
    val pagerState = rememberPagerState(pageCount = { avatars.size })

    BoxWithConstraints {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(avatarSize),
            contentPadding = PaddingValues(horizontal = (maxWidth / 2) - (avatarSize / 2)),
            pageSpacing = 20.dp
        ) { page ->
            Image(
                painter = painterResource(avatars[page]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .border(
                        width = 8.dp,
                        color = Color.White,
                        shape = CircleShape
                    ),
            )
        }
    }
}
