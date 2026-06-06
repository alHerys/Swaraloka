package com.pamt.swarabox.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.pamt.swarabox.ui.components.AppNavigationBar
import com.pamt.swarabox.ui.components.MiniPlayer
import com.pamt.swarabox.ui.navigation.AppNavHost
import com.pamt.swarabox.ui.navigation.Home
import com.pamt.swarabox.ui.navigation.PlayMusic
import com.pamt.swarabox.ui.navigation.Profile
import com.pamt.swarabox.ui.navigation.Upload
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.player.PlayerUiState
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.song.SongUiState
import com.pamt.swarabox.viewmodel.song.SongViewModel

@Composable
fun AppHomeLayout(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    playerViewModel: PlayerViewModel,
    songViewModel: SongViewModel,
    snackbarHostState: SnackbarHostState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentSong by playerViewModel.currentSong.collectAsStateWithLifecycle()
    val playerUiState by playerViewModel.uiState.collectAsStateWithLifecycle()

    val songUiState by songViewModel.uiState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(profileUiState, songUiState) {
        when {
            profileUiState is ProfileUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (profileUiState as ProfileUiState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }

            songUiState is SongUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (songUiState as SongUiState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

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
            val routeWithBottomBar = listOf(Home::class, Profile::class, Upload::class)
            val isHaveBottomBar =
                routeWithBottomBar.any { currentDestination?.hasRoute(it) == true }

            if (isHaveBottomBar) {
                Column {
                    currentSong?.let { song ->
                        MiniPlayer(
                            song = song,
                            isPlaying = playerUiState is PlayerUiState.Playing,
                            onTogglePlay = { playerViewModel.togglePlayPause() },
                            onClick = { navController.navigate(PlayMusic(song)) }
                        )
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
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF212121))
                .background(color = Color(0x33000000))
        ) {
            AnimatedVisibility(
                visible = playerUiState is PlayerUiState.Playing,
                enter = fadeIn(animationSpec = tween(durationMillis = 800)),
                exit = fadeOut(animationSpec = tween(durationMillis = 800))
            ) {
                AnimatedContent(
                    targetState = currentSong,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(800)) togetherWith fadeOut(
                            animationSpec = tween(
                                800
                            )
                        )
                    },
                    label = "BackgroundTransition"
                ) { song ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = song?.thumbnailUrl,
                            placeholder = ColorPainter(Color(0x33000000)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(30.dp),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f))
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    playerViewModel = playerViewModel,
                    songViewModel = songViewModel,
                    snackbarHostState = snackbarHostState,
                    startDestination = Home,
                    profileUiState = profileUiState,
                )
            }
        }
    }
}
