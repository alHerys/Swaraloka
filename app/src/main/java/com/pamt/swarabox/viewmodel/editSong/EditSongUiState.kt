package com.pamt.swarabox.viewmodel.editSong

sealed class EditSongUiState {
    object Idle : EditSongUiState()
    object Loading : EditSongUiState()
    object Success : EditSongUiState()
    data class Error(val message: String) : EditSongUiState()
}