package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pamt.swarabox.R
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.AuthBackground
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit,
) {
    val passwordFocusRequester = remember { FocusRequester() }

    val navigationToRegister = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFCCCCCC),
            )
        ) {
            append("Don’t have an account? ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "LOGIN",
                linkInteractionListener = { onNavigateToRegister() }
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
                append("Register")
            }
        }
    }


    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = modifier
                    .padding(
                        horizontal = 24.dp,
                        vertical = 15.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(15.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "Back Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.height(30.dp))
                Text(
                    "Welcome Back :)",
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
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(
                            top = 15.dp,
                            bottom = 24.dp,
                            start = 18.dp,
                            end = 18.dp,
                        ),
                ) {

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppTextField(
                            value = email,
                            label = "Email",
                            onValueChange = onEmailChange,
                            placeholder = "jeren@example.com",
                            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )
                        AppTextField(
                            value = password,
                            label = "Password",
                            onValueChange = onPasswordChange,
                            placeholder = "..........",
                            modifier = Modifier.focusRequester(passwordFocusRequester),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardActions = KeyboardActions(onDone = { onLogin() }),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                        )
                    }

                    Spacer(Modifier.height(50.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AppButton(
                            text = "Submit",
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color(0xFFF0B505),
                            textColor = Color(0xFF212121),
                            onClick = onLogin,
                            enabled = email.isNotBlank() && password.isNotBlank()
                        )

                        Spacer(Modifier.height(19.dp))

                        Text(text = navigationToRegister)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    SwaraBoxTheme {
        LoginScreen(
            email = "",
            onEmailChange = {},
            password = "",
            onPasswordChange = {},
            onNavigateToRegister = {},
            onLogin = {},
            onBack = {}
        )
    }
}