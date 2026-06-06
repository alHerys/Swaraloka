package com.pamt.swarabox.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.navigation.EditSong
import com.pamt.swarabox.ui.navigation.PlayMusic
import com.pamt.swarabox.ui.theme.formatTime
import com.pamt.swarabox.ui.theme.yellow
import com.pamt.swarabox.viewmodel.player.PlayerUiState
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import com.pamt.swarabox.viewmodel.song.SongViewModel

@Composable
fun PlayMusicScreen(
    song: SongModel,
    userId: String,
    playerViewModel: PlayerViewModel,
    navController: NavController,
    songViewModel: SongViewModel,
) {
    val currentPosition by playerViewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by playerViewModel.duration.collectAsStateWithLifecycle()
    val similarSongs by songViewModel.songs.collectAsStateWithLifecycle()
    val playerUiState by playerViewModel.uiState.collectAsStateWithLifecycle()

    val similarSongsFiltered = similarSongs
        .filter { it.id != song.id }
        .take(5)

    PlayMusicContent(
        song = song,
        userId = userId,
        duration = duration,
        currentPosition = currentPosition,
        isPlaying = playerUiState is PlayerUiState.Playing,
        isBuffering = playerUiState is PlayerUiState.Loading,
        similiarSong = similarSongsFiltered,
        onBack = { navController.popBackStack() },
        onTogglePlayPause = { playerViewModel.togglePlayPause() },
        onNavigateToEdit = { songItem ->
            navController.navigate(EditSong(songItem))
        },
        onNavigateToPlay = { songItem ->
            navController.navigate(PlayMusic(songItem)) {
                popUpTo<PlayMusic> {
                    inclusive = true
                }
            }
        },
        onSeekTo = { newPosition ->
            playerViewModel.seekTo(newPosition)
        },
    )
}

@Composable
fun PlayMusicContent(
    song: SongModel,
    userId: String,
    duration: Long,
    currentPosition: Long,
    isPlaying: Boolean,
    similiarSong: List<SongModel>,
    onBack: () -> Unit,
    onNavigateToEdit: (SongModel) -> Unit,
    onNavigateToPlay: (SongModel) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    isBuffering: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleContainer(
                size = 40.dp,
                backgroundColor = Color(0x33FFFFFF),
                onClick = onBack
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back_icon),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (song.artistId == userId) {
                CircleContainer(
                    size = 40.dp,
                    backgroundColor = Color(0x33FFFFFF),
                    onClick = {
                        onNavigateToEdit(song)
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.edit),
                        contentDescription = "Edit Song",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = song.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = song.artistName ?: "Unknown Artist",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF747474),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Slider(
                value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                onValueChange = {
                    val newPos = (it * duration).toLong()
                    onSeekTo(newPos)
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
                Text(
                    text = formatTime(currentPosition),
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = formatTime(duration),
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF262626))
                .padding(horizontal = 16.dp, vertical = 21.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.play_skip_back),
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onSeekTo(0L) }
                )

                Spacer(modifier = Modifier.width(45.dp))

                CircleContainer(
                    size = 50.dp,
                    backgroundColor = yellow,
                    onClick = onTogglePlayPause
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                if (isPlaying) R.drawable.pause else R.drawable.play
                            ),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(45.dp))

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.play_skip_forward),
                    contentDescription = "Next Song",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            if (similiarSong.isNotEmpty()) {
                                onNavigateToPlay(similiarSong.random())
                            }
                        }
                )
            }

            Text(
                text = "swipe up to find similar music",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFF747474),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                similiarSong.forEach { songItem ->
                    SongTile(
                        song = songItem,
                        containerColor = Color(0xFF1A1A1A),
                        onClick = { onNavigateToPlay(songItem) }
                    )
                }
            }
        }
    }
}

