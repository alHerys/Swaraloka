package com.pamt.swarabox.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AuthBackground
import com.pamt.swarabox.ui.components.LogoWidget
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun LandingScreen(
    onGetStartedClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
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
                linkInteractionListener = { onNavigateToLogin() }
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

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AuthBackground {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    LogoWidget()
                    Spacer(Modifier.height(15.dp))
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
                ) {
                    AppButton(
                        text = "Get Started",
                        textColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onGetStartedClick
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(navigationToLoginText)
                }

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LandingScreenPreview() {
    SwaraBoxTheme {
        LandingScreen()
    }
}
