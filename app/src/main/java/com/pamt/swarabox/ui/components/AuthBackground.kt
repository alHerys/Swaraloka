package com.pamt.swarabox.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.pamt.swarabox.R

@Composable
fun AuthBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Image(
        painter = painterResource(R.drawable.landing_page),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = BiasAlignment(horizontalBias = -0.23f, verticalBias = 0f),
        modifier = Modifier.fillMaxSize()
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color.Black.copy(alpha = 0.4f),
                    )
                )
            ),
        content = { content() }
    )
}