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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
fun RegisterEmailPasswordPage(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }

    val policyAgreementText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFCCCCCC),
            )
        ) {
            append("By continuing, you agree to SwaraBox’s ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "LOGIN",
                linkInteractionListener = { }
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
                append("Condition of Use")
            }
        }
        withStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFCCCCCC),
            )
        ) {
            append(" and ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "LOGIN",
                linkInteractionListener = { }
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
                append("Privacy Notice")
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
                modifier = modifier.padding(
                    horizontal = 24.dp,
                    vertical = 15.dp
                ),
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                IconButton(
                    onClick = { },
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
                    "Enter your email & password",
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
                    topStart = 27.dp,
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
                            value = name,
                            label = "Email",
                            onValueChange = { name = it },
                            placeholder = "jeren@example.com",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        AppTextField(
                            value = name,
                            label = "Passowrd",
                            onValueChange = { name = it },
                            placeholder = "..........",
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        AppTextField(
                            value = name,
                            label = "Confirm Password",
                            onValueChange = { name = it },
                            placeholder = "..........",
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }

                    Spacer(Modifier.height(30.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AppButton(
                            text = "Submit",
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color(0xFFF0B505),
                            textColor = Color(0xFF212121),
                            onClick = {}
                        )

                        Spacer(Modifier.height(19.dp))

                        Text(
                            text = policyAgreementText,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterEmailPasswordPreview() {
    SwaraBoxTheme {
        Scaffold(Modifier.fillMaxSize()) {
            RegisterEmailPasswordPage(
                Modifier.padding(it),
            )
        }
    }
}
