package com.pamt.swarabox.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pamt.swarabox.R

@Composable
fun LogoWidget(modifier: Modifier = Modifier) {
    val logoText = buildAnnotatedString {

        withStyle(
            style = SpanStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight(700),
                color = Color.White
            )
        ) {
            append("Swara")
        }
        withStyle(
            style = SpanStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFEBB77B)
            )
        ) {
            append("Box")
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_swarabox),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Text(text = logoText)
    }
}