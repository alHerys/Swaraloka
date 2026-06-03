package com.pamt.swarabox.viewmodel.uploadSong

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UploadViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun uploadSong(
        title: String,
        artistId: String,
        audioBytes: ByteArray,
        imageBytes: ByteArray,
        duration: Int
    ) {
        viewModelScope.launch {
            _uiState.value = UploadUiState.Loading
            try {
                val audioUrl = repository.uploadAudio(artistId, audioBytes)

                val thumbnailUrl = repository.uploadThumbnail(artistId, imageBytes)

                val song = SongModel(
                    artistId = artistId,
                    title = title,
                    songUrl = audioUrl,
                    thumbnailUrl = thumbnailUrl,
                    songDuration = duration
                )

                repository.insertSong(song)

                _uiState.value = UploadUiState.Success
            } catch (e: Exception) {
                _uiState.value = UploadUiState.Error(e.message ?: "Failed to upload song")
            }
        }
    }

    fun resetState() {
        _uiState.value = UploadUiState.Idle
    }
}
