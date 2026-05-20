package com.example.projectpamt.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.pamt.swarabox.ui.Screen
import com.pamt.swarabox.ui.screens.LandingPage


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    when (authCheckState.value) {
        is AuthCheckState.Authenticated -> {
            MainNavHost(
                authViewModel = authViewModel,
                startDestination = Screen.Home.route
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
                startDestination = Screen.Login.route
            )
        }
    }
}

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val navController = rememberNavController()
    val email = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val fullname = authViewModel.fullname.collectAsStateWithLifecycle()
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) {
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
        composable(Screen.Landing.route) {
            LandingPage(

            ) {  }
        }

        composable(Screen.Login.route) {

        }

        composable(Screen.Register.route) {

        }

        composable(Screen.Home.route) {

        }
    }
}