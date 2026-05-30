package com.pamt.swarabox.ui.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.model.SongModelNavType
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.screens.EditProfileScreen
import com.pamt.swarabox.ui.screens.HomeScreen
import com.pamt.swarabox.ui.screens.LandingScreen
import com.pamt.swarabox.ui.screens.LoginScreen
import com.pamt.swarabox.ui.screens.PlayMusicScreen
import com.pamt.swarabox.ui.screens.ProfileScreen
import com.pamt.swarabox.ui.screens.RegisterEmailPasswordScreen
import com.pamt.swarabox.ui.screens.RegisterNameScreen
import com.pamt.swarabox.ui.screens.UploadScreen
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.song.SongViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import com.pamt.swarabox.viewmodel.upload.UploadUiState
import com.pamt.swarabox.viewmodel.upload.UploadViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import kotlin.reflect.typeOf

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    playerViewModel: PlayerViewModel,
    uploadViewModel: UploadViewModel,
    songViewModel: SongViewModel,
    authUiState: AuthUiState,
    profileUiState: ProfileUiState,
    uploadUiState: UploadUiState,
    snackbarHostState: SnackbarHostState,
    startDestination: Any,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Home> {
            val songs by songViewModel.songs.collectAsStateWithLifecycle()
            val isRefreshing by songViewModel.isRefreshing.collectAsStateWithLifecycle()

            HomeScreen(
                songs = songs,
                isRefreshing = isRefreshing,
                onRefresh = { songViewModel.fetchSongs() },
                onNavigateToPlay = { song ->
                    playerViewModel.playSong(song)
                    navController.navigate(PlayMusic(song))
                },
                modifier = modifier
            )
        }

        composable<PlayMusic>(
            typeMap = mapOf(typeOf<SongModel>() to SongModelNavType)
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<PlayMusic>()
            val similarSongs by songViewModel.songs.collectAsStateWithLifecycle()
            val userId = (profileUiState as? ProfileUiState.Success)?.user?.userId ?: ""

            PlayMusicScreen(
                playerViewModel = playerViewModel,
                song = args.song,
                similarSongs = similarSongs,
                userId = userId,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToPlay = { song ->
                    playerViewModel.playSong(song)
                    navController.navigate(PlayMusic(song)) {
                        popUpTo<PlayMusic> {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable<Upload> {
            UploadScreen(
                profileUiState = profileUiState,
                playerViewModel = playerViewModel,
                uploadViewModel = uploadViewModel,
                snackbarHostState = snackbarHostState,
                navController = navController,
            )
        }

        composable<Landing> {
            LandingScreen(
                onNavigateToLogin = {
                    navController.navigate(Login)
                },
                onGetStartedClicked = {
                    navController.navigate(RegisterName)
                },
                modifier = modifier
            )
        }

        composable<Login> {
            val email by authViewModel.email.collectAsStateWithLifecycle()
            val password by authViewModel.password.collectAsStateWithLifecycle()
            LoginScreen(
                email = email,
                onEmailChange = { authViewModel.onEmailChange(it) },
                password = password,
                onPasswordChange = { authViewModel.onPasswordChange(it) },
                onNavigateToRegister = {
                    authViewModel.resetFormState()
                    navController.navigate(RegisterName) {
                        popUpTo(Login) {
                            inclusive = true
                        }
                    }
                },
                onLogin = {
                    authViewModel.login()
                },
                onBack = {
                    authViewModel.resetFormState()
                    navController.navigate(Landing) {
                        popUpTo(Landing) {
                            inclusive = true
                        }
                    }
                },
                modifier = modifier
            )
        }

        composable<RegisterName> {
            val name by authViewModel.name.collectAsStateWithLifecycle()
            RegisterNameScreen(
                name = name,
                onNameChange = { authViewModel.onNameChange(it) },
                onBack = {
                    navController.navigate(Landing) {
                        popUpTo(RegisterName) {
                            inclusive = true
                        }
                    }
                },
                onNextClicked = {
                    navController.navigate(RegisterEmailPassword)
                },
                onNavigateToLogin = {
                    authViewModel.resetFormState()
                    navController.navigate(Login) {
                        popUpTo(RegisterName) {
                            inclusive = true
                        }
                    }
                },
                modifier = modifier
            )
        }

        composable<RegisterEmailPassword> {
            val email by authViewModel.email.collectAsStateWithLifecycle()
            val password by authViewModel.password.collectAsStateWithLifecycle()
            val confirmPassword by authViewModel.confirmPassword.collectAsStateWithLifecycle()
            RegisterEmailPasswordScreen(
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                onEmailChange = { authViewModel.onEmailChange(it) },
                onPasswordChange = { authViewModel.onPasswordChange(it) },
                onConfirmPasswordChange = { authViewModel.onConfirmPasswordChange(it) },
                onBack = {
                    navController.popBackStack()
                },
                onRegister = {
                    authViewModel.register()
                },
            )
        }

        composable<Profile> {
            if (profileUiState is ProfileUiState.Success) {
                val mySongs by profileViewModel.mySongs.collectAsStateWithLifecycle()
                val isRefreshingSongs by profileViewModel.isRefreshingSongs.collectAsStateWithLifecycle()

                ProfileScreen(
                    user = profileUiState.user,
                    listMySong = mySongs,
                    isRefreshing = isRefreshingSongs,
                    onRefresh = { profileViewModel.fetchMySongs(forceRefresh = true) },
                    onLogout = {
                        profileViewModel.resetUiState()
                        authViewModel.resetFormState()
                        authViewModel.logout()
                    },
                    onNavigateToAbout = {},
                    onNavigateToEdit = {
                        navController.navigate(EditProfile)
                    },
                    onNavigateToPlay = { song ->
                        playerViewModel.playSong(song)
                        navController.navigate(PlayMusic(song))
                    }
                )
            }
        }

        composable<EditProfile> {
            val name by profileViewModel.name.collectAsStateWithLifecycle()
            val avatarUrl by profileViewModel.avatarUrl.collectAsStateWithLifecycle()

            val context = LocalContext.current
            var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

            var isUpdating by remember { mutableStateOf(false) }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    selectedImageUri = it
                    profileViewModel.onAvatarChange(it.toString())
                }
            }

            LaunchedEffect(profileUiState) {
                if (isUpdating) {
                    when (profileUiState) {
                        is ProfileUiState.Success -> {
                            navController.popBackStack()
                            isUpdating = false
                        }

                        is ProfileUiState.Error -> {
                            profileViewModel.resetChanges()
                            isUpdating = false
                        }

                        else -> {}
                    }
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    profileViewModel.resetChanges()
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color(0x33000000),
            ) { innerPadding ->
                EditProfileScreen(
                    name = name,
                    avatar = avatarUrl,
                    modifier = Modifier.padding(innerPadding),
                    onNameChange = { profileViewModel.onNameChange(it) },
                    onAvatarChange = { launcher.launch("image/*") },
                    onEdit = {
                        val imageBytes = selectedImageUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        }
                        isUpdating = true
                        profileViewModel.updateProfile(name, avatarUrl.ifEmpty { null }, imageBytes)
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )

                if (profileUiState is ProfileUiState.Loading) {
                    LoadingOverlay()
                }
            }
        }
    }
}
