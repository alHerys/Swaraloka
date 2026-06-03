package com.pamt.swarabox.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.UserModel
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.viewmodel.editProfile.EditProfileUiState
import com.pamt.swarabox.viewmodel.editProfile.EditProfileViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    currentUser: UserModel,
    profileViewModel: ProfileViewModel,
    editProfileViewModel: EditProfileViewModel = viewModel(),
    snackbarHostState: SnackbarHostState,
    navController: NavController,
) {
    val editProfileUiState by editProfileViewModel.uiState.collectAsStateWithLifecycle()
    val name by editProfileViewModel.name.collectAsStateWithLifecycle()
    val selectedImageUri by editProfileViewModel.selectedImageUri.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) editProfileViewModel.onImageChange(uri)
    }

    LaunchedEffect(Unit) {
        editProfileViewModel.onNameChange(currentUser.name)
    }

    LaunchedEffect(editProfileUiState) {
        when (editProfileUiState) {
            is EditProfileUiState.Success -> {
                launch {
                    snackbarHostState.showSnackbar(
                        message = "Profile Updated Successfully",
                        duration = SnackbarDuration.Short
                    )
                }
                profileViewModel.fetchProfile()
                navController.popBackStack()
            }

            is EditProfileUiState.Error -> {
                launch {
                    snackbarHostState.showSnackbar(
                        message = (editProfileUiState as EditProfileUiState.Error).message,
                        duration = SnackbarDuration.Short
                    )
                }
                Log.d(
                    "EDIT PROFILE ERROR",
                    (editProfileUiState as EditProfileUiState.Error).message
                )
            }

            else -> {}
        }
    }

    EditProfileContent(
        name = name,
        avatar = selectedImageUri?.toString() ?: currentUser.avatarUrl ?: "",
        modifier = modifier,
        onNameChange = { editProfileViewModel.onNameChange(it) },
        onAvatarChange = { launcher.launch("image/*") },
        onEdit = {
            val imageBytes = selectedImageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }
            editProfileViewModel.updateProfile(
                userId = currentUser.userId,
                name = name,
                oldAvatarUrl = currentUser.avatarUrl,
                imageBytes = imageBytes
            )
        },
        onCancel = {
            navController.popBackStack()
        },
        isLoading = editProfileUiState is EditProfileUiState.Loading
    )

    DisposableEffect(Unit) {
        onDispose {
            editProfileViewModel.resetState()
        }
    }
}

@Composable
fun EditProfileContent(
    modifier: Modifier = Modifier,
    name: String,
    onNameChange: (String) -> Unit,
    onAvatarChange: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    avatar: String,
    isLoading: Boolean = false,
) {
    Box(modifier = Modifier.fillMaxSize()) {
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

        if (isLoading) {
            LoadingOverlay()
        }
    }
}
