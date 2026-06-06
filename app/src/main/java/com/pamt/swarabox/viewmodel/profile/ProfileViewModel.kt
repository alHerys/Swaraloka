package com.pamt.swarabox.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.repository.ProfileRepository
import com.pamt.swarabox.data.repository.SongRepository
import com.pamt.swarabox.ui.theme.convertMessage
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository(),
    private val songRepository: SongRepository = SongRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _mySongs = MutableStateFlow<List<SongModel>>(emptyList())
    val mySongs = _mySongs.asStateFlow()

    private val _isRefreshingSongs = MutableStateFlow(false)
    val isRefreshingSongs = _isRefreshingSongs.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val user = repository.getCurrentProfile(userId)
                    _uiState.value = ProfileUiState.Success(user)
                    fetchMySongs()
                } else {
                    _uiState.value = ProfileUiState.Error("Sesi kamu telah berakhir. Silakan masuk kembali.")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.convertMessage())
            }
        }
    }

    fun fetchMySongs(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
            if (userId != null) {
                _isRefreshingSongs.value = true
                try {
                    val songs = songRepository.fetchSongsByArtist(
                        artistId = userId,
                        forceRefresh = forceRefresh
                    )
                    _mySongs.value = songs
                } catch (e: Exception) {
                    _uiState.value = ProfileUiState.Error(e.convertMessage())
                } finally {
                    _isRefreshingSongs.value = false
                }
            }
        }
    }

    fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}