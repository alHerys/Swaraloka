package com.pamt.swarabox.viewmodel.editProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.repository.ProfileRepository
import com.pamt.swarabox.ui.theme.convertMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onImageChange(value: Uri?) {
        _selectedImageUri.value = value
    }

    fun resetState() {
        _name.value = ""
        _selectedImageUri.value = null
        _uiState.value = EditProfileUiState.Idle
    }

    fun updateProfile(
        userId: String,
        name: String,
        oldAvatarUrl: String?,
        imageBytes: ByteArray? = null
    ) {
        _uiState.value = EditProfileUiState.Loading
        viewModelScope.launch {
            try {
                var finalAvatarUrl = oldAvatarUrl

                if (imageBytes != null) {
                    finalAvatarUrl = repository.uploadAvatar(userId, imageBytes)
                }

                repository.editUserProfile(userId, name, finalAvatarUrl)

                _uiState.value = EditProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(
                    message = e.convertMessage()
                )
            }
        }
    }
}
