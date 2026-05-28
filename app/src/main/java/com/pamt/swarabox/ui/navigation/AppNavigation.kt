package com.pamt.swarabox.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.viewmodel.auth.AuthCheckState
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.song.SongUploadUiState
import com.pamt.swarabox.viewmodel.song.SongUploadViewModel
import com.pamt.swarabox.viewmodel.song.SongViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    songViewModel: SongViewModel = viewModel(),
    songUploadViewModel: SongUploadViewModel = viewModel()
) {
    val authCheckState by authViewModel.authCheckState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val songUploadUiState by songUploadViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    LaunchedEffect(authCheckState) {
        if (authCheckState is AuthCheckState.Authenticated) {
            authViewModel.clearUiState()
            profileViewModel.fetchProfile()
        } else if (authCheckState is AuthCheckState.NotAuthenticated) {
            songViewModel.release()
        }
    }

    LaunchedEffect(authUiState, profileUiState, songUploadUiState) {
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

            songUploadUiState is SongUploadUiState.Error -> {
                val message = (songUploadUiState as SongUploadUiState.Error).message
                Log.d("UPLOAD ERROR", message)
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
            AppBottomNavLayout(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                songViewModel = songViewModel,
                songUploadViewModel = songUploadViewModel,
                authUiState = authUiState,
                profileUiState = profileUiState,
                snackbarHostState = snackbarHostState,
                songUploadUiState = songUploadUiState,
            )
        }

        is AuthCheckState.NotAuthenticated -> {
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
                        songViewModel = songViewModel,
                        songUploadViewModel = songUploadViewModel,
                        startDestination = Landing,
                        snackbarHostState = snackbarHostState,
                        authUiState = authUiState,
                        profileUiState = profileUiState,
                        songUploadUiState = songUploadUiState,
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

        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color(0xFF262626),
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}