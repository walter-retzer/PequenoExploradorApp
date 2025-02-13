package com.example.pequenoexploradorapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun AvatarImagesHorizontalPager(
    pagerState: PagerState,
    avatars: List<Int>
) {
    val avatarSize = 180.dp

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
