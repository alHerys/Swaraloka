package com.pamt.swarabox.viewmodel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.UserModel
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

    private var lastSuccessUser: UserModel? = null

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
                    
                    // Menambahkan timestamp untuk mem-bypass cache Coil
                    val timestamp = System.currentTimeMillis()
                    val userWithCacheBuster = user.copy(
                        avatarUrl = user.avatarUrl?.let { "$it?t=$timestamp" }
                    )

                    _name.value = userWithCacheBuster.name
                    _avatarUrl.value = userWithCacheBuster.avatarUrl ?: ""

                    _uiState.value = ProfileUiState.Success(userWithCacheBuster)
                    lastSuccessUser = userWithCacheBuster
                } else {
                    _uiState.value = ProfileUiState.Error("Session end, please relogged to app")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error while fetching profile")
            }
        }
    }

    fun updateProfile(name: String, avatarUrl: String?, newImageBytes: ByteArray? = null) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id

                if (userId != null) {
                    var finalAvatarUrl = avatarUrl

                    if (newImageBytes != null) {
                        finalAvatarUrl = repository.uploadAvatar(userId, newImageBytes)
                    }

                    repository.editUserProfile(userId, name, finalAvatarUrl)

                    val user = repository.getCurrentProfile(userId)

                    val timestamp = System.currentTimeMillis()
                    val userWithCacheBuster = user.copy(
                        avatarUrl = user.avatarUrl?.let { "$it?t=$timestamp" }
                    )

                    _name.value = userWithCacheBuster.name
                    _avatarUrl.value = userWithCacheBuster.avatarUrl ?: ""

                    _uiState.value = ProfileUiState.Success(userWithCacheBuster)
                    lastSuccessUser = userWithCacheBuster
                } else {
                    _uiState.value = ProfileUiState.Error("Session end, please relogged to app")
                }
            } catch (e: Exception) {
                Log.d("UPDATE PROFILE", e.message.toString())
                _uiState.value = ProfileUiState.Error(e.message ?: "Error while updating profile")
            }
        }
    }

    fun resetChanges() {
        lastSuccessUser?.let { user ->
            _name.value = user.name
            _avatarUrl.value = user.avatarUrl ?: ""
            _uiState.value = ProfileUiState.Success(user)
        }
    }

    fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}
