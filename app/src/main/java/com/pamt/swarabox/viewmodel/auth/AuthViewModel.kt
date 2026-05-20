package com.example.projectpamt.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _name = MutableStateFlow("")
    val fullname: StateFlow<String> = _name

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

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

    fun onPhoneChange(value: String) {
        _phone.value = value
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
            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}