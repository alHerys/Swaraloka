package com.pamt.swarabox.viewmodel.editSong

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditSongViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {



    private val _uiState = MutableStateFlow<EditSongUiState>(EditSongUiState.Idle)
    val uiState: StateFlow<EditSongUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = EditSongUiState.Idle
    }

    fun updateSong(
        songId: String,
        title: String?,
        audioBytes: ByteArray?,
        imageBytes: ByteArray?,
        duration: Int?
    ) {
        _uiState.value = EditSongUiState.Loading
        viewModelScope.launch {
            try {
//                repository.updateSong(song)
                _uiState.value = EditSongUiState.Success
            } catch (e: Exception) {
                _uiState.value = EditSongUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}