package com.pamt.swarabox.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pamt.swarabox.R
import com.pamt.swarabox.ui.theme.SwaraBoxTheme
import com.pamt.swarabox.ui.widget.AuthBackground

@Composable
fun RegisterNamePage(
    modifier: Modifier = Modifier,
    onNextClick: (String) -> Unit = {}
) {
    var name by remember { mutableStateOf("") }

    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = modifier.padding(
                    horizontal = 24.dp,
                    vertical = 5.dp
                ),
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                IconButton(
                    onClick = {},
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
                color = Color(0xff262626)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 45.dp,
                            bottom = 24.dp,
                            start = 24.dp,
                            end = 24.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFCCCCCC),
                    )
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(70.dp),

                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color(0x33FFFFFF),
                            disabledContainerColor = Color.Gray,
                            cursorColor = Color.Black
                        ),
                        placeholder = {
                            Text(
                                "Enter your name",
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFFFFFFFF),
                            )
                        }
                    )
                    Button(
                        onClick = { onNextClick(name) },
                        modifier = modifier.fillMaxWidth(),
                    ) {
                        Text("Next")
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
        Scaffold(Modifier.fillMaxSize()) {
            RegisterNamePage(Modifier.padding(it))
        }
    }
}
