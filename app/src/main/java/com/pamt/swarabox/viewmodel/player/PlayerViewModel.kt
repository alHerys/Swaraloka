package com.pamt.swarabox.viewmodel.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.pamt.swarabox.data.model.SongModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _exoPlayer = ExoPlayer.Builder(application).build()

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _currentSong = MutableStateFlow<SongModel?>(null)
    val currentSong: StateFlow<SongModel?> = _currentSong.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var progressJob: Job? = null

    init {
        // listiner untuk memantau state dari exoplayer
        _exoPlayer.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                _uiState.value = when {
                    player.playbackState == Player.STATE_BUFFERING -> PlayerUiState.Loading
                    player.isPlaying -> PlayerUiState.Playing
                    else -> PlayerUiState.Idle
                }

                if (player.playbackState == Player.STATE_READY) {
                    _duration.value = player.duration.coerceAtLeast(0L)
                }

                if (player.isPlaying) {
                    startProgressUpdate()
                } else stopProgressUpdate()
            }
        })
    }

    fun playSong(song: SongModel) {
        // Jika lagu sama dan sedang di play, abaikan saja
        // Tapi jika lagu sama dan di pause, play kembali lagi itu
        if (_currentSong.value?.id == song.id) {
            if (!_exoPlayer.isPlaying) _exoPlayer.play()
            return
        }

        // Siapkan Exoplayer dan play lagu begitu siap
        _currentSong.value = song
        _exoPlayer.apply {
            setMediaItem(MediaItem.fromUri(song.songUrl))
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        if (_exoPlayer.isPlaying) {
            _exoPlayer.pause()
        } else {
            _exoPlayer.play()
        }
    }

    fun pause() {
        if (_exoPlayer.isPlaying) {
            _exoPlayer.pause()
        }
    }

    fun seekTo(position: Long) {
        _exoPlayer.seekTo(position)
        _currentPosition.value = position
    }

    // Fungsi untuk progres lagu
    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                _currentPosition.value = _exoPlayer.currentPosition
                delay(1000) // artinya, progres akan berskala 1 detik
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    // Membersihkan seluruh state exoplayer
    fun release() {
        _exoPlayer.stop()
        _exoPlayer.clearMediaItems()
        _currentSong.value = null
        _uiState.value = PlayerUiState.Idle
        _currentPosition.value = 0L
        _duration.value = 0L
        stopProgressUpdate()
    }

    // Ini dipakai di ketika logout (viewmodel dibuang)
    override fun onCleared() {
        super.onCleared()
        _exoPlayer.release()
        stopProgressUpdate()
    }
}