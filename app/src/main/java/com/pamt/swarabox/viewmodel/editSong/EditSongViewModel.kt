package com.pamt.swarabox.viewmodel.editSong

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditSongViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditSongUiState>(EditSongUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _selectedAudioUri = MutableStateFlow<Uri?>(null)
    val selectedAudioUri = _selectedAudioUri.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    private val _duration = MutableStateFlow<Long?>(null)
    val duration = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onAudioChange(value: Uri?) {
        _selectedAudioUri.value = value
    }

    fun onImageChange(value: Uri?) {
        _selectedImageUri.value = value
    }

    fun onDurationChange(value: Long) {
        _duration.value = value
    }

    fun onIsPlayingChange(value: Boolean) {
        _isPlaying.value = value
    }

    fun onCurrentPositionChange(value: Long) {
        _currentPosition.value = value
    }

    fun resetState() {
        _title.value = ""
        _selectedAudioUri.value = null
        _selectedImageUri.value = null
        _duration.value = null
        _isPlaying.value = false
        _currentPosition.value = 0L
        _uiState.value = EditSongUiState.Idle
    }

    fun updateSong(
        songId: String,
        oldSongUrl: String,
        oldThumbnailUrl: String,
        artistId: String,
        title: String?,
        audioBytes: ByteArray?,
        imageBytes: ByteArray?,
        duration: Int?
    ) {
        _uiState.value = EditSongUiState.Loading
        viewModelScope.launch {
            try {
                repository.updateSong(
                    songId = songId,
                    title = title,
                    duration = duration,
                    artistId = artistId,
                    oldSongUrl = oldSongUrl,
                    oldThumbnailUrl = oldThumbnailUrl,
                    audioBytes = audioBytes,
                    imageBytes = imageBytes
                )
                _uiState.value = EditSongUiState.Success
            } catch (e: Exception) {
                _uiState.value = EditSongUiState.Error(
                    message = e.message ?: "Error occured while updating song"
                )
            }
        }
    }

    fun deleteSong(
        songId: String,
        songUrl: String,
        thumbnailUrl: String
    ) {
        _uiState.value = EditSongUiState.Loading
        viewModelScope.launch {
            try {
                repository.deleteSong(
                    songId = songId,
                    songUrl = songUrl,
                    thumbnailUrl = thumbnailUrl
                )
                _uiState.value = EditSongUiState.Success
            } catch (e: Exception) {
                _uiState.value = EditSongUiState.Error(
                    message = e.message ?: "Error occured while deleting song"
                )
            }
        }
    }
}