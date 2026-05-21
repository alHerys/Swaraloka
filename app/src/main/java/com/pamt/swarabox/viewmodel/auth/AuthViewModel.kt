package com.pamt.swarabox.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectpamt.viewmodel.auth.AuthCheckState
import com.pamt.swarabox.data.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _authCheckState = MutableStateFlow<AuthCheckState>(AuthCheckState.Checking)
    val authCheckState: StateFlow<AuthCheckState> = _authCheckState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    init {
        observeAuthStatus()
    }

    private fun observeAuthStatus() {
        viewModelScope.launch {
            repository.sessionStatus.collect { status ->
                _authCheckState.value = when (status) {
                    is SessionStatus.Authenticated -> AuthCheckState.Authenticated
                    is SessionStatus.NotAuthenticated -> AuthCheckState.NotAuthenticated
                    is SessionStatus.Initializing -> AuthCheckState.Checking
                    is SessionStatus.RefreshFailure -> {
                        // Jika refresh gagal (misal koneksi internet), tetap cek session yang ada
                        // atau anggap tidak terautentikasi jika session expired.
                        if (repository.isLoggedIn()) AuthCheckState.Authenticated
                        else AuthCheckState.NotAuthenticated
                    }
                }
            }
        }
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
    }

    fun login() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                repository.login(
                    email = _email.value,
                    password = _password.value
                )
                _uiState.value = AuthUiState.Success

            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    message = e.message ?: "Login gagal"
                )
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading

                repository.register(
                    fullname = _name.value,
                    email = _email.value,
                    password = _password.value
                )

                _uiState.value = AuthUiState.Success

            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    message = e.message ?: "Register gagal"
                )
                Log.d("SUPABASE_REGISTER", e.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            resetAllState()
        }
    }

    fun clearUiState() {
        _uiState.value = AuthUiState.Idle
    }

    fun resetAllState() {
        _name.value = ""
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
    }
}