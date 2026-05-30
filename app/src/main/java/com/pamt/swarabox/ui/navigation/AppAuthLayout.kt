package com.pamt.swarabox.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.song.SongViewModel
import com.pamt.swarabox.viewmodel.upload.UploadUiState
import com.pamt.swarabox.viewmodel.upload.UploadViewModel

@Composable
fun AppAuthLayout(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    playerViewModel: PlayerViewModel,
    uploadViewModel: UploadViewModel,
    songViewModel: SongViewModel,
    snackbarHostState: SnackbarHostState,
    authUiState: AuthUiState,
    profileUiState: ProfileUiState,
    uploadUiState: UploadUiState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isError = data.visuals.message.contains("Error", ignoreCase = true) ||
                        data.visuals.message.contains("Failed", ignoreCase = true)
                Snackbar(
                    snackbarData = data,
                    containerColor = if(isError) Color(0xFFF04444) else Color(0xFF4CAF50),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavHost(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                playerViewModel = playerViewModel,
                uploadViewModel = uploadViewModel,
                songViewModel = songViewModel,
                startDestination = Landing,
                snackbarHostState = snackbarHostState,
                authUiState = authUiState,
                profileUiState = profileUiState,
                uploadUiState = uploadUiState,
                modifier = Modifier.padding(innerPadding),
            )

            val isLoading =
                authUiState is AuthUiState.Loading || authUiState is AuthUiState.Success

            if (isLoading) {
                LoadingOverlay()
            }
        }
    }
}