package com.pamt.swarabox.viewmodel.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SongUploadViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongUploadUiState>(SongUploadUiState.Idle)
    val uiState: StateFlow<SongUploadUiState> = _uiState

    fun uploadSong(
        title: String,
        artistId: String,
        audioBytes: ByteArray,
        imageBytes: ByteArray,
        duration: Int
    ) {
        viewModelScope.launch {
            _uiState.value = SongUploadUiState.Loading
            try {
                val audioUrl = repository.uploadAudio(artistId, audioBytes)

                val thumbnailUrl = repository.uploadThumbnail(artistId, imageBytes)

                repository.insertSong(
                    artistId = artistId,
                    title = title,
                    songUrl = audioUrl,
                    thumbnailUrl = thumbnailUrl,
                    duration = duration
                )

                _uiState.value = SongUploadUiState.Success
            } catch (e: Exception) {
                _uiState.value = SongUploadUiState.Error(e.message ?: "Failed to upload song")
            }
        }
    }

    fun resetState() {
        _uiState.value = SongUploadUiState.Idle
    }
}
