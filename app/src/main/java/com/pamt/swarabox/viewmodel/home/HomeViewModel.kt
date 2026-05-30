package com.pamt.swarabox.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SongRepository = SongRepository()
) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongModel>>(emptyList())
    val songs: StateFlow<List<SongModel>> = _songs.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    init {
        fetchSongs()
    }

    fun fetchSongs() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val fetchedSongs = repository.fetchAllSongs()
                _songs.value = fetchedSongs
            } catch (e: Exception) {
                _errorEvent.emit(e.message ?: "Failed to fetch songs")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
