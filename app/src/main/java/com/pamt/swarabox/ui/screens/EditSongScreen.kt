package com.pamt.swarabox.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.DashedSelector
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.navigation.EditSong
import com.pamt.swarabox.ui.navigation.Home
import com.pamt.swarabox.ui.theme.formatTime
import com.pamt.swarabox.viewmodel.editSong.EditSongUiState
import com.pamt.swarabox.viewmodel.editSong.EditSongViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun EditSongScreen(
    currentSong: SongModel,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    editSongViewModel: EditSongViewModel = viewModel(),
) {
    val editSongUiState by editSongViewModel.uiState.collectAsStateWithLifecycle()
    val title by editSongViewModel.title.collectAsStateWithLifecycle()
    val selectedAudioUri by editSongViewModel.selectedAudioUri.collectAsStateWithLifecycle()
    val selectedImageUri by editSongViewModel.selectedImageUri.collectAsStateWithLifecycle()
    val duration by editSongViewModel.duration.collectAsStateWithLifecycle()
    val isPlaying by editSongViewModel.isPlaying.collectAsStateWithLifecycle()
    val localPlayerCurrentPosition by editSongViewModel.currentPosition.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val localPlayer = remember { ExoPlayer.Builder(context).build() }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) editSongViewModel.onAudioChange(uri)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) editSongViewModel.onImageChange(uri)
    }

    LaunchedEffect(Unit) {
        editSongViewModel.onTitleChange(currentSong.title)
    }

    LaunchedEffect(selectedAudioUri) {
        if (selectedAudioUri != null) {
            localPlayer.setMediaItem(MediaItem.fromUri(selectedAudioUri!!))
            localPlayer.prepare()
        } else {
            localPlayer.stop()
            localPlayer.clearMediaItems()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                editSongViewModel.onCurrentPositionChange(localPlayer.currentPosition)
                delay(1000)
            }
        }
    }

    LaunchedEffect(editSongUiState) {
        when (editSongUiState) {
            is EditSongUiState.Success -> {
                launch {
                    snackbarHostState.showSnackbar(
                        message = "Song Edited Successfully",
                        duration = SnackbarDuration.Short
                    )
                }

                navController.navigate(Home(isForcedRefresh = true)) {
                    popUpTo<EditSong> {
                        inclusive = true
                    }
                }
            }

            is EditSongUiState.Error -> {
                launch {
                    snackbarHostState.showSnackbar(
                        message = (editSongUiState as EditSongUiState.Error).message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            else -> {}
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                editSongViewModel.onIsPlayingChange(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    editSongViewModel.onDurationChange(localPlayer.duration.coerceAtLeast(0L))
                }
            }
        }

        localPlayer.addListener(listener)

        onDispose {
            localPlayer.removeListener(listener)
            localPlayer.release()
            editSongViewModel.resetState()
        }
    }

    EditSongContent(
        editSongUiState = editSongUiState,
        oldTitle = currentSong.title,
        isPlaying = isPlaying,
        title = title,
        selectedAudioUri = selectedAudioUri,
        selectedImageUri = selectedImageUri,
        duration = duration,
        currentPosition = localPlayerCurrentPosition,
        onPlaySong = {
            playerViewModel.pause()
            localPlayer.play()
        },
        onPauseSong = { localPlayer.pause() },
        onAudioChange = { audioPicker.launch("audio/*") },
        onImageChange = { imagePicker.launch("image/*") },
        onTitleChange = { newTitle ->
            editSongViewModel.onTitleChange(newTitle)
        },
        onSeek = { newPosition ->
            localPlayer.seekTo(newPosition)
            editSongViewModel.onCurrentPositionChange(newPosition)
        },
        onDelete = {
            editSongViewModel.deleteSong(
                songId = currentSong.id!!,
                songUrl = currentSong.songUrl,
                thumbnailUrl = currentSong.thumbnailUrl
            )
        },
        onCancel = { navController.popBackStack() },
        onEdit = {
            val audioBytes = selectedAudioUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }
            val imageBytes = selectedImageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }

            editSongViewModel.updateSong(
                songId = currentSong.id!!,
                artistId = currentSong.artistId!!,
                title = title,
                audioBytes = audioBytes,
                imageBytes = imageBytes,
                duration = duration?.toInt(),
                oldSongUrl = currentSong.songUrl,
                oldThumbnailUrl = currentSong.thumbnailUrl,
            )
        }
    )
}

@Composable
private fun EditSongContent(
    editSongUiState: EditSongUiState,
    oldTitle: String,
    duration: Long?,
    title: String,
    selectedAudioUri: Uri?,
    selectedImageUri: Uri?,
    currentPosition: Long,
    isPlaying: Boolean,
    onPlaySong: () -> Unit,
    onPauseSong: () -> Unit,
    onSeek: (Long) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onEdit: () -> Unit,
    onAudioChange: () -> Unit,
    onImageChange: () -> Unit,
    onTitleChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onCancel,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.back_icon),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Edit Song",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        contentDescription = "Back",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }

            }

            Spacer(Modifier.height(28.dp))

            if (selectedImageUri != null) {

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF262626))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { onImageChange() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                            contentDescription = "Edit Image",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                DashedSelector(
                    label = "Select New Image",
                    icon = ImageVector.vectorResource(id = R.drawable.image_upload),
                    onClick = { onImageChange() }
                )
            }

            if (selectedAudioUri != null && duration != null) {
                Spacer(modifier = Modifier.height(40.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                        onValueChange = {
                            val newPosition = (it * duration).toLong()
                            onSeek(newPosition)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.height(4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(currentPosition), color = Color.White, fontSize = 12.sp)
                        Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(modifier = Modifier.weight(1f))

                        CircleContainer(
                            size = 50.dp,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            onClick = {
                                if (isPlaying) {
                                    onPauseSong()
                                } else {
                                    onPlaySong()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    if (isPlaying) R.drawable.pause else R.drawable.play
                                ),
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            IconButton(onClick = onAudioChange) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                                    contentDescription = "Replace Audio",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(21.dp))

                DashedSelector(
                    label = "Select New .mp3 or .m4a",
                    icon = ImageVector.vectorResource(id = R.drawable.song_upload),
                    onClick = onAudioChange
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            AppTextField(
                value = title,
                onValueChange = onTitleChange,
                label = "Title",
                placeholder = "Enter song title"
            )

            Spacer(modifier = Modifier.height(48.dp))

            AppButton(
                onClick = onEdit,
                text = "Edit",
                containerColor = Color(0xFF007AFF),
                textColor = Color.White,
                enabled = (
                        (title.isNotBlank() && title.trim() != oldTitle || selectedImageUri != null || selectedAudioUri != null)
                                && editSongUiState !is EditSongUiState.Loading
                        ),
                modifier = Modifier.width(175.dp)
            )
            Spacer(Modifier.height(40.dp))
        }

        if (editSongUiState is EditSongUiState.Loading) {
            LoadingOverlay()
        }
    }
}