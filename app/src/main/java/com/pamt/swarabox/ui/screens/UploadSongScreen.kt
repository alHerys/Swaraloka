package com.pamt.swarabox.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.ui.navigation.Home
import com.pamt.swarabox.ui.navigation.Upload
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.DashedSelector
import com.pamt.swarabox.ui.components.LoadingOverlay
import com.pamt.swarabox.ui.theme.formatTime
import com.pamt.swarabox.viewmodel.profile.ProfileUiState
import com.pamt.swarabox.viewmodel.uploadSong.UploadUiState
import com.pamt.swarabox.viewmodel.uploadSong.UploadViewModel
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun UploadSongScreen(
    profileUiState: ProfileUiState,
    playerViewModel: PlayerViewModel,
    uploadViewModel: UploadViewModel,
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {
    val context = LocalContext.current
    val songUploadUiState by uploadViewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val localPlayer = remember { ExoPlayer.Builder(context).build() }
    var isLocalPlayerPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    val artistId = (profileUiState as? ProfileUiState.Success)?.user?.userId ?: ""

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAudioUri = uri
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingChanged: Boolean) {
                isLocalPlayerPlaying = isPlayingChanged
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    duration = localPlayer.duration.coerceAtLeast(0L)
                }
            }
        }
        localPlayer.addListener(listener)
        onDispose {
            localPlayer.removeListener(listener)
            localPlayer.release()
        }
    }

    LaunchedEffect(songUploadUiState) {
        if (songUploadUiState is UploadUiState.Success) {
            launch {
                snackbarHostState.showSnackbar(
                    message = "Song Uploaded Successfully",
                    duration = SnackbarDuration.Short
                )
            }
            uploadViewModel.resetState()

            navController.navigate(Home) {
                popUpTo<Upload> {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(selectedAudioUri) {
        if (selectedAudioUri != null) {
            localPlayer.setMediaItem(MediaItem.fromUri(selectedAudioUri!!))
            localPlayer.prepare()
        } else {
            localPlayer.stop()
            localPlayer.clearMediaItems()
            currentPosition = 0L
            duration = 0L
        }
    }

    LaunchedEffect(isLocalPlayerPlaying) {
        if (isLocalPlayerPlaying) {
            while (isActive) {
                currentPosition = localPlayer.currentPosition
                delay(1000)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x33000000))
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = "Upload New Song",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

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
                            .clickable { selectedImageUri = null },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.cancel),
                            contentDescription = "Edit Image",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                DashedSelector(
                    label = "Select Image",
                    icon = ImageVector.vectorResource(id = R.drawable.image_upload),
                    onClick = { imagePicker.launch("image/*") }
                )
            }

            if (selectedAudioUri != null) {
                Spacer(modifier = Modifier.height(40.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                        onValueChange = {
                            val newPos = (it * duration).toLong()
                            localPlayer.seekTo(newPos)
                            currentPosition = newPos
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
                                if (localPlayer.isPlaying) {
                                    localPlayer.pause()
                                } else {
                                    playerViewModel.pause()
                                    localPlayer.play()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    if (isLocalPlayerPlaying) R.drawable.pause else R.drawable.play
                                ),
                                contentDescription = if (isLocalPlayerPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            IconButton(onClick = { selectedAudioUri = null }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.cancel),
                                    contentDescription = "Replace Audio",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(21.dp))

                DashedSelector(
                    label = "Select .mp3 or .m4a",
                    icon = ImageVector.vectorResource(id = R.drawable.song_upload),
                    onClick = { audioPicker.launch("audio/*") }
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                placeholder = "Enter song title"
            )

            Spacer(modifier = Modifier.height(48.dp))

            AppButton(
                onClick = {
                    val audioBytes = selectedAudioUri?.let { uri ->
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    }
                    val imageBytes = selectedImageUri?.let { uri ->
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    }

                    if (audioBytes != null && imageBytes != null) {
                        uploadViewModel.uploadSong(
                            title = title,
                            artistId = artistId,
                            audioBytes = audioBytes,
                            imageBytes = imageBytes,
                            duration = duration.toInt()
                        )
                    }
                },
                text = "Upload",
                containerColor = Color(0xFF007AFF),
                textColor = Color.White,
                enabled = (selectedImageUri != null && selectedAudioUri != null && title.isNotBlank() && songUploadUiState !is UploadUiState.Loading),
                modifier = Modifier.width(175.dp)
            )
            Spacer(Modifier.height(40.dp))
        }

        if (songUploadUiState is UploadUiState.Loading) {
            LoadingOverlay()
        }
    }
}
