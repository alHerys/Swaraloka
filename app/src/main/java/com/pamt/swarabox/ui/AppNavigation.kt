package com.pamt.swarabox.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.model.SongModelNavType
import com.pamt.swarabox.ui.components.AppNavigationBar
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
import com.pamt.swarabox.viewmodel.auth.AuthCheckState
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.profile.ProfileViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val authCheckState by authViewModel.authCheckState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    LaunchedEffect(authCheckState) {
        if (authCheckState is AuthCheckState.Authenticated) {
            authViewModel.clearUiState()
            profileViewModel.fetchProfile()
        }
    }

    when (authCheckState) {
        is AuthCheckState.Authenticated -> {
            AuthenticatedLayout(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                authUiState = authUiState,
                profileUiState = profileUiState
            )
        }

        is AuthCheckState.NotAuthenticated -> {
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavHost(
                    navController = navController,
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

@Composable
fun AuthenticatedLayout(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    authUiState: AuthUiState,
    profileUiState: ProfileUiState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            // Only show bottom bar on main screens (Home, Profile, etc.)
            val showBottomBar = currentDestination?.route?.contains("Home") == true ||
                    currentDestination?.route?.contains("Profile") == true ||
                    currentDestination?.route?.contains("Upload") == true

            if (showBottomBar) {
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
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            MainNavHost(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                startDestination = Home
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

@Composable
fun MainNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    startDestination: Any
) {
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val errorMessage = when {
        authUiState is AuthUiState.Error -> {
            Log.d("AUTH ERROR", (authUiState as AuthUiState.Error).message)
            (authUiState as AuthUiState.Error).message
        }

        profileUiState is ProfileUiState.Error -> {
            Log.d("PROFILE ERROR", (profileUiState as ProfileUiState.Error).message)
            (profileUiState as ProfileUiState.Error).message
        }

        else -> null
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Home> {
            HomeScreen(
                onNavigateToPlay = { song ->
                    navController.navigate(PlayMusic(song))
                }
            )
        }

        composable<PlayMusic>(
            typeMap = mapOf(typeOf<SongModel>() to SongModelNavType)
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<PlayMusic>()
            PlayMusicScreen(
                song = args.song,
                similarSongs = SongModel.dummyList,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToPlay = { song ->
                    navController.navigate(PlayMusic(song)) {
                        popUpTo<PlayMusic> {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable<Upload> {
            UploadScreen(profileUiState = profileUiState)
        }

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
                    },
                    onNavigateToPlay = { song ->
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

            val snackbarHostState = remember { SnackbarHostState() }

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
                            val message = (profileUiState as ProfileUiState.Error).message
                            launch {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                                profileViewModel.resetChanges()
                                isUpdating = false
                            }
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
                containerColor = Color(0xFF262626),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = Color(0xFFF04444),
                            contentColor = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
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
