package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun EditProfileScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onAvatarChange: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    avatar: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit Profile",
            fontSize = 18.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFFFFFFFF),
        )

        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .clickable { onAvatarChange() }
        ) {
            CircleContainer(
                backgroundColor = Color(0xFF262626),
                size = 120.dp,
                onClick = onAvatarChange
            ) {
                AsyncImage(
                    model = avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomEnd)
                    .clickable { onAvatarChange() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.edit),
                    contentDescription = "Edit Profile Picture",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        AppTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Enter new name",
            label = "Name",
        )

        Spacer(Modifier.height(32.dp))

        AppButton(
            onClick = onEdit,
            text = "Edit",
            containerColor = Color(0xFF007AFF),
            textColor = Color.White,
            modifier = Modifier.width(175.dp)
        )

        Spacer(Modifier.height(12.dp))

        AppButton(
            onClick = onCancel,
            text = "Cancel",
            containerColor = Color(0xFFF04444),
            textColor = Color.White,
            modifier = Modifier.width(175.dp)
        )
    }
}

@Preview(showSystemUi = true, name = "Empty Avatar", showBackground = true)
@Composable
private fun EditProfileScreenEmptyPreview() {
    SwaraBoxTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF262626),
        ) {
            EditProfileScreen(
                name = "John Doe",
                avatar = "",
                onNameChange = {},
                onAvatarChange = {},
                onEdit = {},
                onCancel = {},
                modifier = Modifier.padding(it)
            )
        }
    }
}
