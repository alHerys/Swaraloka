package com.pamt.swarabox.viewmodel.song

sealed class SongUiState {
    object Idle : SongUiState()
    object Loading : SongUiState()
    object Success : SongUiState()
    data class Error(val message: String) : SongUiState()
}
