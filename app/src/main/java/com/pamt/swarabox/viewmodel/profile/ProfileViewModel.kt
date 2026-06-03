package com.pamt.swarabox.viewmodel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.model.UserModel
import com.pamt.swarabox.data.repository.ProfileRepository
import com.pamt.swarabox.data.repository.SongRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository(),
    private val songRepository: SongRepository = SongRepository()
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> =_name.asStateFlow()

    private val _avatarUrl = MutableStateFlow("")
    val avatarUrl: StateFlow<String> = _avatarUrl.asStateFlow()

    private val _mySongs = MutableStateFlow<List<SongModel>>(emptyList())
    val mySongs: StateFlow<List<SongModel>> = _mySongs.asStateFlow()

    private val _isRefreshingSongs = MutableStateFlow(false)
    val isRefreshingSongs: StateFlow<Boolean> = _isRefreshingSongs.asStateFlow()

    private val _songErrorEvent = MutableSharedFlow<String>()
    val songErrorEvent: SharedFlow<String> = _songErrorEvent.asSharedFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var lastSuccessUser: UserModel? = null

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onAvatarChange(value: String) {
        _avatarUrl.value = value
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val user = repository.getCurrentProfile(userId)
                    
                    // Menambahkan timestamp untuk mem-bypass cache Coil
                    val timestamp = System.currentTimeMillis()
                    val userWithCacheBuster = user.copy(
                        avatarUrl = user.avatarUrl?.let { "$it?t=$timestamp" }
                    )

                    _name.value = userWithCacheBuster.name
                    _avatarUrl.value = userWithCacheBuster.avatarUrl ?: ""

                    _uiState.value = ProfileUiState.Success(userWithCacheBuster)
                    lastSuccessUser = userWithCacheBuster
                    
                    // Fetch songs too
                    fetchMySongs()
                } else {
                    _uiState.value = ProfileUiState.Error("Session end, please relogged to app")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error while fetching profile")
            }
        }
    }

    fun fetchMySongs(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
            if (userId != null) {
                _isRefreshingSongs.value = true
                try {
                    val songs = songRepository.fetchSongsByArtist(userId, forceRefresh = forceRefresh)
                    _mySongs.value = songs
                } catch (e: Exception) {
                    _songErrorEvent.emit(e.message ?: "Failed to fetch your songs")
                } finally {
                    _isRefreshingSongs.value = false
                }
            }
        }
    }

    fun updateProfile(name: String, avatarUrl: String?, newImageBytes: ByteArray? = null) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id

                if (userId != null) {
                    var finalAvatarUrl = avatarUrl

                    if (newImageBytes != null) {
                        finalAvatarUrl = repository.uploadAvatar(userId, newImageBytes)
                    }

                    repository.editUserProfile(userId, name, finalAvatarUrl)

                    val user = repository.getCurrentProfile(userId)

                    val timestamp = System.currentTimeMillis()
                    val userWithCacheBuster = user.copy(
                        avatarUrl = user.avatarUrl?.let { "$it?t=$timestamp" }
                    )

                    _name.value = userWithCacheBuster.name
                    _avatarUrl.value = userWithCacheBuster.avatarUrl ?: ""

                    _uiState.value = ProfileUiState.Success(userWithCacheBuster)
                    lastSuccessUser = userWithCacheBuster
                } else {
                    _uiState.value = ProfileUiState.Error("Session end, please relogged to app")
                }
            } catch (e: Exception) {
                Log.d("UPDATE PROFILE", e.message.toString())
                _uiState.value = ProfileUiState.Error(e.message ?: "Error while updating profile")
            }
        }
    }

    fun resetChanges() {
        lastSuccessUser?.let { user ->
            _name.value = user.name
            _avatarUrl.value = user.avatarUrl ?: ""
            _uiState.value = ProfileUiState.Success(user)
        }
    }

    fun resetUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}
