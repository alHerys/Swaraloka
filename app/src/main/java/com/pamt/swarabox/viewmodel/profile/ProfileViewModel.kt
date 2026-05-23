package com.pamt.swarabox.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.repository.ProfileRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> =_name

    private val _avatarUrl = MutableStateFlow("")
    val avatarUrl: StateFlow<String> = _avatarUrl

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onAvatarChange(value: String) {
        _avatarUrl.value = value
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val user = repository.getCurrentProfile(userId)

                    _name.value = user.name
                    _avatarUrl.value = user.avatarUrl ?: ""

                    _uiState.value = ProfileUiState.Success(user)
                } else {
                    _uiState.value = ProfileUiState.Error("Session end, please relogged to app")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error while fetching profile")
            }
        }
    }

    fun updateProfile(name: String, avatarUrl: String?) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    repository.editUserProfile(userId, name, avatarUrl)

                    val user = repository.getCurrentProfile(userId)

                    _name.value = user.name
                    _avatarUrl.value = user.avatarUrl ?: ""

                    _uiState.value = ProfileUiState.Success(user)
                } else {
                    _uiState.value = ProfileUiState.Error("Session end, please relogged to app")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error while updating profile")
            }
        }
    }

    fun cancelEdit() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            _name.value = currentState.user.name
            _avatarUrl.value = currentState.user.avatarUrl ?: ""
        }
    }

    fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}
