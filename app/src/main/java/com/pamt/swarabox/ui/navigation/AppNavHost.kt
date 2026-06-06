package com.pamt.swarabox.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.model.UserModel
import com.pamt.swarabox.ui.screens.home.AboutScreen
import com.pamt.swarabox.ui.screens.home.EditProfileScreen
import com.pamt.swarabox.ui.screens.home.EditSongScreen
import com.pamt.swarabox.ui.screens.home.HomeScreen
import com.pamt.swarabox.ui.screens.auth.LandingScreen
import com.pamt.swarabox.ui.screens.auth.LoginScreen
import com.pamt.swarabox.ui.screens.home.PlayMusicScreen
import com.pamt.swarabox.ui.screens.home.ProfileScreen
import com.pamt.swarabox.ui.screens.auth.RegisterEmailPasswordScreen
import com.pamt.swarabox.ui.screens.auth.RegisterNameScreen
import com.pamt.swarabox.ui.screens.home.UploadSongScreen
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.song.SongViewModel
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel? = null,
    playerViewModel: PlayerViewModel? = null,
    songViewModel: SongViewModel? = null,
    profileUiState: ProfileUiState? = null,
    snackbarHostState: SnackbarHostState,
    startDestination: Any,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Landing> {
            LandingScreen(
                modifier = modifier,
                onNavigateToLogin = {
                    navController.navigate(Login)
                },
                onGetStartedClicked = {
                    navController.navigate(RegisterName)
                },
            )
        }

        composable<Login> {
            LoginScreen(
                modifier = modifier,
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable<RegisterName> {
            RegisterNameScreen(
                modifier = modifier,
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable<RegisterEmailPassword> {
            RegisterEmailPasswordScreen(
                modifier = modifier,
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable<Home> {
            HomeScreen(
                modifier = modifier,
                songViewModel = songViewModel!!,
                navController = navController
            )
        }

        composable<PlayMusic>(
            typeMap = mapOf(typeOf<SongModel>() to SongModel.SongModelNavType)
        ) { backStackEntry ->
            val song = backStackEntry.toRoute<PlayMusic>().song
            val userId = (profileUiState as? ProfileUiState.Success)?.user?.userId ?: ""

            LaunchedEffect(song) {
                playerViewModel!!.playSong(song)
            }

            PlayMusicScreen(
                song = song,
                userId = userId,
                playerViewModel = playerViewModel!!,
                songViewModel = songViewModel!!,
                navController = navController,
            )
        }

        composable<EditSong>(
            typeMap = mapOf(typeOf<SongModel>() to SongModel.SongModelNavType)
        ) { backStackEntry ->
            val currentSong = backStackEntry.toRoute<EditSong>().currentSong
            EditSongScreen(
                currentSong = currentSong,
                snackbarHostState = snackbarHostState,
                playerViewModel = playerViewModel!!,
                profileViewModel = profileViewModel!!,
                songViewModel = songViewModel!!,
                navController = navController,
            )
        }

        composable<Upload> {
            UploadSongScreen(
                profileUiState = profileUiState!!,
                playerViewModel = playerViewModel!!,
                profileViewModel = profileViewModel!!,
                snackbarHostState = snackbarHostState,
                songViewModel = songViewModel!!,
                navController = navController,
            )
        }

        composable<About> {
            AboutScreen(navController = navController)
        }

        composable<Profile> {
            ProfileScreen(
                profileViewModel = profileViewModel!!,
                authViewModel = authViewModel,
                navController = navController,
            )
        }

        composable<EditProfile>(
            typeMap = mapOf(typeOf<UserModel>() to UserModel.UserModelNavType)
        ) { backStackEntry ->
            val currentUser = backStackEntry.toRoute<EditProfile>().currentUser
            EditProfileScreen(
                currentUser = currentUser,
                modifier = modifier,
                profileViewModel = profileViewModel!!,
                snackbarHostState = snackbarHostState,
                navController = navController
            )
        }
    }
}
