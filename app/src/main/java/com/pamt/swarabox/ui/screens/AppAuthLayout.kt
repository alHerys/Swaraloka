package com.pamt.swarabox.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.navigation.AppNavHost
import com.pamt.swarabox.ui.navigation.Landing
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import com.pamt.swarabox.viewmodel.auth.AuthViewModel

@Composable
fun AppAuthLayout(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.Error) {
            Log.d("AUTH ERROR", (authUiState as AuthUiState.Error).message)
            snackbarHostState.showSnackbar(
                message = (authUiState as AuthUiState.Error).message,
                actionLabel = "error",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isError = data.visuals.actionLabel == "error"
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) Color(0xFFF04444) else Color(0xFF4CAF50),
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
                startDestination = Landing,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(innerPadding),
            )

            val isLoading = authUiState is AuthUiState.Loading || authUiState is AuthUiState.Success

            if (isLoading) {
                LoadingOverlay()
            }
        }
    }
}