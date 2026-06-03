package com.pamt.swarabox.viewmodel.uploadSong

import android.net.Uri
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

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _selectedAudioUri = MutableStateFlow<Uri?>(null)
    val selectedAudioUri = _selectedAudioUri.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    private val _isLocalPlayerPlaying = MutableStateFlow(false)
    val isLocalPlayerPlaying = _isLocalPlayerPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onAudioSelected(uri: Uri) {
        _selectedAudioUri.value = uri
    }

    fun onAudioRemove() {
        _selectedAudioUri.value = null
        _currentPosition.value = 0L
        _duration.value = 0L
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun onImageRemove() {
        _selectedImageUri.value = null
    }

    fun onIsLocalPlayerPlayingChange(value: Boolean) {
        _isLocalPlayerPlaying.value = value
    }

    fun onCurrentPositionChange(value: Long) {
        _currentPosition.value = value
    }

    fun onDurationChange(value: Long) {
        _duration.value = value
    }

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
                repository.insertSong(
                    title = title,
                    artistId = artistId,
                    audioBytes = audioBytes,
                    imageBytes = imageBytes,
                    duration = duration
                )
                _uiState.value = UploadUiState.Success
            } catch (e: Exception) {
                _uiState.value = UploadUiState.Error(e.message ?: "Failed to upload song")
            }
        }
    }

    fun resetState() {
        _uiState.value = UploadUiState.Idle
        _title.value = ""
        _selectedAudioUri.value = null
        _selectedImageUri.value = null
        _isLocalPlayerPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
    }
}
