package com.pamt.swarabox.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pamt.swarabox.ui.components.AppNavigationBar
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.components.MiniPlayer
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.song.SongViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.upload.UploadUiState
import com.pamt.swarabox.viewmodel.upload.UploadViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppHomeLayout(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    playerViewModel: PlayerViewModel,
    uploadViewModel: UploadViewModel,
    songViewModel: SongViewModel,
    authUiState: AuthUiState,
    profileUiState: ProfileUiState,
    uploadUiState: UploadUiState,
    snackbarHostState: SnackbarHostState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentSong by playerViewModel.currentSong.collectAsStateWithLifecycle()
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val currentDestination = navBackStackEntry?.destination
    val isKeyboardVisible = WindowInsets.isImeVisible

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isError = data.visuals.message.contains("Error", ignoreCase = true) ||
                        data.visuals.message.contains("Failed", ignoreCase = true)
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) Color(0xFFF04444) else Color(0xFF4CAF50),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        bottomBar = {
            // Only show bottom bar on main screens (Home, Profile, etc.)
            val showBottomBar = (currentDestination?.route?.contains("Home") == true ||
                    currentDestination?.route?.contains("Profile") == true ||
                    currentDestination?.route?.contains("Upload") == true) &&
                    !isKeyboardVisible

            if (showBottomBar) {
                Column {
                    AnimatedVisibility(
                        visible = currentSong != null,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.background(Color.Transparent)
                    ) {
                        currentSong?.let { song ->
                            MiniPlayer(
                                song = song,
                                isPlaying = isPlaying,
                                onTogglePlay = { playerViewModel.togglePlayPause() },
                                onClick = { navController.navigate(PlayMusic(song)) }
                            )
                        }
                    }

                    AppNavigationBar(
                        navController = navController,
                        currentDestination = currentDestination,
                        containerColor = Color(0xFF212121),
                        contentColor = Color.White,
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            AppNavHost(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                playerViewModel = playerViewModel,
                uploadViewModel = uploadViewModel,
                songViewModel = songViewModel,
                snackbarHostState = snackbarHostState,
                startDestination = Home,
                authUiState = authUiState,
                profileUiState = profileUiState,
                uploadUiState = uploadUiState,
            )

            val isLoading =
                authUiState is AuthUiState.Loading ||
                        profileUiState is ProfileUiState.Loading ||
                        profileUiState is ProfileUiState.Idle

            if (isLoading) {
                LoadingOverlay()
            }
        }
    }
}
