package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.model.UserModel
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun ProfileScreen(
    user: UserModel,
    listMySong: List<SongModel>,
    onLogout: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x33000000))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CircleContainer(
                size = 38.dp,
                backgroundColor = Color(0xFF343434),
                onClick = onNavigateToEdit
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }


        CircleContainer(
            size = 120.dp,
            backgroundColor = Color.Gray
        ) {
            if (user.avatarUrl != null) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                val initials = user.name.split(" ").filter { it.isNotBlank() }.take(2)
                    .joinToString("") { it.take(1).uppercase() }
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
            }
        }

        Text(
            text = user.name,
            fontSize = 22.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFFFFFFFF),
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = user.email,
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF747474),
        )

        Spacer(Modifier.height(40.dp))

        Column(Modifier.fillMaxWidth()) {
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.library_music
                    ),
                    contentDescription = null,
                    tint = Color.White,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "My Collections",
                    fontSize = 18.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                )
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listMySong) { song ->
                    SongTile(song = song)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onNavigateToAbout,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x33FEFEFE)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.about),
                    tint = Color.White,
                    contentDescription = null
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "About SwaraBox",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(500),
                    color = Color.White,
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        AppButton(
            onClick = onLogout,
            text = "Logout",
            containerColor = Color(0xFFF04444),
            textColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    SwaraBoxTheme {
        ProfileScreen(
            user = UserModel.dummy,
            listMySong = SongModel.dummyList,
            onLogout = {},
            onNavigateToAbout = { },
            onNavigateToEdit = { }
        )
    }
}