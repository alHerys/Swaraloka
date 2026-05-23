package com.pamt.swarabox.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.screens.EditProfileScreen
import com.pamt.swarabox.ui.screens.LandingScreen
import com.pamt.swarabox.ui.screens.LoginScreen
import com.pamt.swarabox.ui.screens.ProfileScreen
import com.pamt.swarabox.ui.screens.RegisterEmailPasswordScreen
import com.pamt.swarabox.ui.screens.RegisterNameScreen
import com.pamt.swarabox.viewmodel.auth.AuthCheckState
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val authCheckState by authViewModel.authCheckState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Trigger fetch profile saat user terdeteksi sudah Authenticated
    LaunchedEffect(authCheckState) {
        if (authCheckState is AuthCheckState.Authenticated) {
            authViewModel.clearUiState()
            profileViewModel.fetchProfile()
        }
    }

    when (authCheckState) {
        is AuthCheckState.Authenticated -> {
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavHost(
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    startDestination = Profile
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

        is AuthCheckState.NotAuthenticated -> {
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavHost(
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    startDestination = Landing
                )

                val isLoading =
                    authUiState is AuthUiState.Loading || authUiState is AuthUiState.Success

                if (isLoading) {
                    LoadingOverlay()
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF262626)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        }
    }
}

@Composable
fun MainNavHost(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    startDestination: Any
) {
    val navController = rememberNavController()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val errorMessage = when {
        authUiState is AuthUiState.Error -> {
            Log.d("AUTH ERROR", (authUiState as AuthUiState.Error).message)
            (authUiState as AuthUiState.Error).message
        }

        profileUiState is ProfileUiState.Error -> {
            Log.d("AUTH ERROR", (profileUiState as ProfileUiState.Error).message)
            (profileUiState as ProfileUiState.Error).message
        }

        else -> null
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Landing> {
            LandingScreen(
                onNavigateToLogin = {
                    navController.navigate(Login)
                },
                onGetStartedClicked = {
                    navController.navigate(RegisterName)
                },
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
                errorMessage = errorMessage
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
                }
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
                errorMessage = errorMessage
            )
        }

        composable<Profile> {
            val state = profileUiState

            if (state is ProfileUiState.Success) {
                ProfileScreen(
                    user = state.user,
                    listMySong = SongModel.dummyList,
                    onLogout = {
                        profileViewModel.resetUiState()
                        authViewModel.resetFormState()
                        authViewModel.logout()
                    },
                    onNavigateToAbout = {},
                    onNavigateToEdit = {
                        navController.navigate(EditProfile)
                    }
                )
            }
        }

        composable<EditProfile> {
            val name by profileViewModel.name.collectAsStateWithLifecycle()
            val avatarUrl by profileViewModel.avatarUrl.collectAsStateWithLifecycle()
            val context = LocalContext.current
            var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    selectedImageUri = it
                    profileViewModel.onAvatarChange(it.toString())
                }
            }

            LaunchedEffect(Unit) {
                profileViewModel.onAvatarChange("")
            }

            DisposableEffect(Unit) {
                onDispose {
                    profileViewModel.resetChanges()
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                EditProfileScreen(
                    name = name,
                    avatar = avatarUrl,
                    onNameChange = { profileViewModel.onNameChange(it) },
                    onAvatarChange = { launcher.launch("image/*") },
                    onEdit = {
                        val imageBytes = selectedImageUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        }
                        profileViewModel.updateProfile(name, avatarUrl.ifEmpty { null }, imageBytes)
                        navController.popBackStack()
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
