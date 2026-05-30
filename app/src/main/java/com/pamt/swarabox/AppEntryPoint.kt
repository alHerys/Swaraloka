package com.pamt.swarabox

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.navigation.AppAuthLayout
import com.pamt.swarabox.ui.navigation.AppHomeLayout
import com.pamt.swarabox.viewmodel.auth.AuthCheckState
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.song.SongUiState
import com.pamt.swarabox.viewmodel.song.SongViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.upload.UploadUiState
import com.pamt.swarabox.viewmodel.upload.UploadViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun AppEntryPoint(
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel(),
    uploadViewModel: UploadViewModel = viewModel(),
    songViewModel: SongViewModel = viewModel()
) {
    val authCheckState by authViewModel.authCheckState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val songUploadUiState by uploadViewModel.uiState.collectAsStateWithLifecycle()
    val homeUiState by songViewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    LaunchedEffect(authCheckState) {
        if (authCheckState is AuthCheckState.Authenticated) {
            authViewModel.clearUiState()
            profileViewModel.fetchProfile()
            songViewModel.fetchSongs()
        } else if (authCheckState is AuthCheckState.NotAuthenticated) {
            playerViewModel.release()
        }
    }

    LaunchedEffect(authUiState, profileUiState, songUploadUiState, homeUiState) {
        when {
            authUiState is AuthUiState.Error -> {
                val message = (authUiState as AuthUiState.Error).message
                Log.d("AUTH ERROR", message)
                launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            profileUiState is ProfileUiState.Error -> {
                val message = (profileUiState as ProfileUiState.Error).message
                Log.d("PROFILE ERROR", message)
                launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            songUploadUiState is UploadUiState.Error -> {
                val message = (songUploadUiState as UploadUiState.Error).message
                Log.d("UPLOAD ERROR", message)
                launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            homeUiState is SongUiState.Error -> {
                val message = (homeUiState as SongUiState.Error).message
                Log.d("HOME ERROR", message)
                launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    when (authCheckState) {
        is AuthCheckState.Authenticated -> {
            AppHomeLayout(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                playerViewModel = playerViewModel,
                uploadViewModel = uploadViewModel,
                songViewModel = songViewModel,
                authUiState = authUiState,
                profileUiState = profileUiState,
                snackbarHostState = snackbarHostState,
                uploadUiState = songUploadUiState,
            )
        }

        is AuthCheckState.NotAuthenticated -> {
            AppAuthLayout(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                playerViewModel = playerViewModel,
                uploadViewModel = uploadViewModel,
                songViewModel = songViewModel,
                snackbarHostState = snackbarHostState,
                authUiState = authUiState,
                profileUiState = profileUiState,
                uploadUiState = songUploadUiState
            )
        }

        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color(0xFF262626),
            ) { innerPadding ->
                LoadingOverlay(Modifier.padding(innerPadding))
            }
        }
    }
}