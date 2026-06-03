package com.pamt.swarabox.viewmodel.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongModel>>(emptyList())
    val songs: StateFlow<List<SongModel>> = _songs.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow<SongUiState>(SongUiState.Idle)
    val uiState: StateFlow<SongUiState> = _uiState.asStateFlow()

    fun clearUiState() {
        _uiState.value = SongUiState.Idle
    }

    fun onIsRefreshingChange(value: Boolean) {
        _isRefreshing.value = value
    }

    fun fetchSongs() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val fetchedSongs = repository.fetchAllSongs(forceRefresh = true)
                _songs.value = fetchedSongs.shuffled()
                _uiState.value = SongUiState.Success
            } catch (e: Exception) {
                _uiState.value = SongUiState.Error(e.message ?: "Failed to fetch songs")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
