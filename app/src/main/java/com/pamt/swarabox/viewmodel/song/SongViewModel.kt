package com.pamt.swarabox.viewmodel.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.repository.SongRepository
import com.pamt.swarabox.ui.theme.convertMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongModel>>(emptyList())
    val songs: StateFlow<List<SongModel>> = _songs.asStateFlow()

    private val _uiState = MutableStateFlow<SongUiState>(SongUiState.Idle)
    val uiState: StateFlow<SongUiState> = _uiState.asStateFlow()

    fun fetchSongs() {
        viewModelScope.launch {
            _uiState.value = SongUiState.Loading
            try {
                val fetchedSongs = repository.fetchAllSongs(forceRefresh = true)
                _songs.value = fetchedSongs.shuffled()
                _uiState.value = SongUiState.Success
            } catch (e: Exception) {
                _uiState.value = SongUiState.Error(e.convertMessage())
            }
        }
    }
}
