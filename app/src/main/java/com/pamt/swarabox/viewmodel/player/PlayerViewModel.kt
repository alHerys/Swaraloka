package com.pamt.swarabox.viewmodel.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.pamt.swarabox.data.model.SongModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _exoPlayer = ExoPlayer.Builder(application).build()

    private val _currentSong = MutableStateFlow<SongModel?>(null)
    val currentSong: StateFlow<SongModel?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var progressJob: Job? = null

    init {
        _exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingChanged: Boolean) {
                _isPlaying.value = isPlayingChanged
                if (isPlayingChanged) {
                    startProgressUpdate()
                } else {
                    stopProgressUpdate()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    _duration.value = _exoPlayer.duration.coerceAtLeast(0L)
                }
            }
        })
    }

    fun playSong(song: SongModel) {
        if (_currentSong.value?.id == song.id) {
            if (!_isPlaying.value) {
                _exoPlayer.play()
            }
            return
        }

        _currentSong.value = song
        val mediaItem = MediaItem.fromUri(song.songUrl)
        _exoPlayer.setMediaItem(mediaItem)
        _exoPlayer.prepare()
        _exoPlayer.playWhenReady = true
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

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                _currentPosition.value = _exoPlayer.currentPosition
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        _exoPlayer.stop()
        _exoPlayer.clearMediaItems()
        _currentSong.value = null
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
        stopProgressUpdate()
    }

    override fun onCleared() {
        super.onCleared()
        _exoPlayer.release()
        stopProgressUpdate()
    }
}