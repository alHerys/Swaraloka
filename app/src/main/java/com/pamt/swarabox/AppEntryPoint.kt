package com.pamt.swarabox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.navigation.AppAuthLayout
import com.pamt.swarabox.ui.navigation.AppHomeLayout
import com.pamt.swarabox.viewmodel.auth.AuthCheckState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.song.SongViewModel

@Composable
fun AppEntryPoint(
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel(),
    songViewModel: SongViewModel = viewModel(),
) {
    val authCheckState by authViewModel.authCheckState.collectAsStateWithLifecycle()
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

    when (authCheckState) {
        is AuthCheckState.Authenticated -> {
            AppHomeLayout(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                playerViewModel = playerViewModel,
                songViewModel = songViewModel,
                snackbarHostState = snackbarHostState,
            )
        }

        is AuthCheckState.NotAuthenticated -> {
            AppAuthLayout(
                navController = navController,
                authViewModel = authViewModel,
                snackbarHostState = snackbarHostState,
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