package com.pamt.swarabox.viewmodel.uploadSong

sealed class UploadUiState {
    object Idle : UploadUiState()
    object Loading : UploadUiState()
    object Success : UploadUiState()
    data class Error(val message: String) : UploadUiState()
}
