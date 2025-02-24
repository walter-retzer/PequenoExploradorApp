package com.example.pequenoexploradorapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pequenoexploradorapp.presentation.theme.mainColor


@Composable
fun ProgressButton(
    modifier: Modifier = Modifier,
    text: String = "",
    isLoading: Boolean = false,
    enable: Boolean = true,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)

    if (isLoading) {
        Box(
            modifier = modifier
                .height(64.dp),
            contentAlignment = Alignment.Center
        ) {
            MyAppCircularProgressIndicator()
        }
    }

    if (isLoading.not()) {
        Button(
            modifier = modifier
                .height(54.dp)
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        0f to mainColor,
                        1f to mainColor
                    )
                ),
            shape = shape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            onClick = { onClick() },
            enabled = enable
        ) {
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
