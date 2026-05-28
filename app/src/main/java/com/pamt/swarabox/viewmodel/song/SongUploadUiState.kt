package com.pamt.swarabox.viewmodel.song

sealed class SongUploadUiState {
    object Idle : SongUploadUiState()
    object Loading : SongUploadUiState()
    object Success : SongUploadUiState()
    data class Error(val message: String) : SongUploadUiState()
}
