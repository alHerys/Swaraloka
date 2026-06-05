package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pamt.swarabox.R
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun AboutScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleContainer(
                size = 38.dp,
                backgroundColor = Color(0xFF343434),
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.back_icon),
                    tint = Color.White,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "About Swaraloka",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 38.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }


        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 280.dp, height = 412.dp),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(1.dp, Color(0xCCEEEEEE))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.creator),
                    contentDescription = "Swaraloka Creator",
                    modifier = Modifier
                        .size(244.dp)
                        .clip(RoundedCornerShape(30.dp))
                )
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Alvianto Hery Sarborn",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF9F9F9)
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = "I'm a mobile development enthusiast currently studying at Brawijaya University.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF929292),
                        lineHeight = 21.sp
                    )
                }
            }
        }


        Text(
            text = "Made with ❤ by Hery | 2026",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF929292),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    SwaraBoxTheme {
        AboutScreen(rememberNavController())
    }
}
