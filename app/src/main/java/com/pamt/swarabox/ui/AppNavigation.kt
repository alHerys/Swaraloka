package com.pamt.swarabox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectpamt.viewmodel.auth.AuthCheckState
import com.example.projectpamt.viewmodel.auth.AuthUiState
import com.example.projectpamt.viewmodel.auth.AuthViewModel
import com.pamt.swarabox.ui.screens.LandingScreen
import com.pamt.swarabox.ui.screens.LoginScreen
import com.pamt.swarabox.ui.screens.RegisterEmailPasswordScreen
import com.pamt.swarabox.ui.screens.RegisterNameScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    when (authCheckState.value) {
        is AuthCheckState.Authenticated -> {
            MainNavHost(
                authViewModel = authViewModel,
                startDestination = Home
            )
        }

        is AuthCheckState.Checking -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthCheckState.NotAuthenticated -> {
            MainNavHost(
                authViewModel = authViewModel,
                startDestination = Login
            )
        }
    }
}

@Composable
fun MainNavHost(
    authViewModel: AuthViewModel,
    startDestination: Any
) {
    val navController = rememberNavController()
    val email by authViewModel.email.collectAsStateWithLifecycle()
    val password by authViewModel.password.collectAsStateWithLifecycle()
    val name by authViewModel.name.collectAsStateWithLifecycle()
    val confirmPassword by authViewModel.confirmPassword.collectAsStateWithLifecycle()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            navController.navigate(Home) {
                popUpTo<Login> {
                    inclusive = true
                }
            }
            authViewModel.resetState()
        }
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
                onGetStartedClick = {
                    navController.navigate(RegisterName)
                },
            )
        }

        composable<Login> {
            LoginScreen(
                email = email,
                onEmailChange = { authViewModel.onEmailChange(it) },
                password = password,
                onPasswordChange = { authViewModel.onPasswordChange(it) },
                onNavigateToRegister = {
                    navController.navigate(RegisterName)
                },
                onLoginClick = {
                    authViewModel.login()
                },
                onBackClicked = { navController.navigate(Landing) },
            )
        }

        composable<RegisterName> {
            RegisterNameScreen(
                name = name,
                onNameChange = { authViewModel.onNameChange(it) },
                onBackClicked = {
                    navController.popBackStack()
                },
                onNextClicked = {
                    navController.navigate(RegisterEmailPassword)
                },
                onNavigateToLogin = {
                    navController.navigate(Login)
                }
            )
        }

        composable<RegisterEmailPassword> {
            RegisterEmailPasswordScreen(
                email = email,
                password = password,
                confirmPassword = password,
                onEmailChange = {authViewModel.onEmailChange(it)},
                onPasswordChange = {authViewModel.onPasswordChange(it)},
                onConfirmPasswordChange = {authViewModel.onPasswordChange(it)}, // TODO: Implement Confirm Passowrd
                onBackClicked = {
                    navController.popBackStack()
                },
                onRegisterClicked = {
                    authViewModel.register()
                }
            )
        }

        composable<Home> {

        }
    }
}