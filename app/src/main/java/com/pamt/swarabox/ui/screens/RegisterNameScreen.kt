package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.pamt.swarabox.R
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.AuthBackground
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun RegisterNameScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onNextClicked: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClicked: () -> Unit,
) {
    val navigationToLogin = buildAnnotatedString {
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

    Scaffold(Modifier.fillMaxSize()) { innerPadding ->
        AuthBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(
                            horizontal = 24.dp,
                            vertical = 15.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(26.dp)
                ) {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier.size(15.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.back_icon),
                            contentDescription = "Back Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        "What's your name?",
                        fontSize = 53.sp,
                        lineHeight = 64.sp,
                        fontWeight = FontWeight(500),
                        color = Color.White,
                    )
                }

                Surface(
                    color = Color(0xff262626),
                    shape = RoundedCornerShape(
                        topEnd = 27.dp,
                        topStart = 27.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .padding(
                                top = 15.dp,
                                bottom = 24.dp,
                                start = 18.dp,
                                end = 18.dp,
                            ),
                    ) {

                        AppTextField(
                            value = name,
                            label = "Name",
                            onValueChange = onNameChange,
                            placeholder = "Your Name",
                        )

                        Spacer(Modifier.height(50.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { onNextClicked(name) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonColors(
                                    containerColor = Color(0xFFF0B505),
                                    contentColor = Color(0xFF212121),
                                    disabledContainerColor = Color.Transparent,
                                    disabledContentColor = Color.Transparent
                                ),
                                contentPadding = PaddingValues(vertical = 16.dp),
                            ) {
                                Text(
                                    "Next",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(500),
                                )
                            }

                            Spacer(Modifier.height(19.dp))

                            Text(text = navigationToLogin)
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterNamePreview() {
    SwaraBoxTheme {
        RegisterNameScreen(
            onNextClicked = { },
            onNavigateToLogin = { },
            name = "",
            onNameChange = {},
            onBackClicked = { }
        )

    }
}
