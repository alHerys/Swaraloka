package com.pamt.swarabox.viewmodel.profile

import com.pamt.swarabox.data.model.UserModel

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: UserModel) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
