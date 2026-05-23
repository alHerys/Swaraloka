package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.UserModel
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    name: String,
    avatar: String? = null,
    onNameChange: (String) -> Unit,
    onAvatarChange: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
) {
    Scaffold(
        Modifier.fillMaxSize(),
        containerColor = Color(0xFF262626)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircleContainer(
                backgroundColor = Color(0xFF262626),
                size = 120.dp,
                onClick = onAvatarChange,
            ) {
//                if (avatar != null) {
//                    Image(
//                        painter = painterResource(
//                            id = TODO()
//                        ),
//                        contentDescription = null
//                    )
//                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 4.dp,
                            color = Color(0xFFE4E0D8),
                            shape = CircleShape
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.image_upload),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Select Image",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFFFFFF),
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
}

@Preview(showSystemUi = true)
@Composable
private fun EditProfileScreenPreview() {
    SwaraBoxTheme {
        EditProfileScreen(
            name = "",
            avatar = "",
            onNameChange = {},
            onAvatarChange = {},
            onEdit = {},
            onCancel = {},
        )
    }
}