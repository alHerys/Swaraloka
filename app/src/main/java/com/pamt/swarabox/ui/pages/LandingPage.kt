package com.pamt.swarabox.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pamt.swarabox.ui.theme.SwaraBoxTheme
import com.pamt.swarabox.ui.components.AuthBackground
import com.pamt.swarabox.ui.components.LogoWidget

@Composable
fun LandingPage(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val navigationToLoginText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFCCCCCC),
            )
        ) {
            append("Already have an account? ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "LOGIN",
                linkInteractionListener = { onLoginClick() }
            )
        ) {
            withStyle(
                style = SpanStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color.White,
                    textDecoration = TextDecoration.Underline,
                )
            ) {
                append("Login")
            }
        }
    }

    AuthBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                LogoWidget()
                Text(
                    text = "Millions of songs Free for You",
                    color = Color.White,
                    fontWeight = FontWeight(500),
                    fontSize = 53.sp,
                    lineHeight = 64.sp,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Button(
                    onClick = onGetStartedClick,
                    colors = ButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight(500),
                    )
                }
                Text(navigationToLoginText)
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LandingPagePreview() {
    SwaraBoxTheme {
        Scaffold(Modifier.fillMaxSize()) {
            LandingPage(Modifier.padding(it))
        }
    }
}
