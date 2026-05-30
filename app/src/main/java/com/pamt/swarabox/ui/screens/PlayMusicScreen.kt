package com.pamt.swarabox.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.theme.SwaraBoxTheme
import com.pamt.swarabox.ui.theme.yellow
import com.pamt.swarabox.viewmodel.song.SongViewModel

@Composable
fun PlayMusicScreen(
    songViewModel: SongViewModel,
    song: SongModel,
    similarSongs: List<SongModel>,
    onNavigateToPlay: (SongModel) -> Unit,
    onBack: () -> Unit
) {
    val isPlaying by songViewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by songViewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by songViewModel.duration.collectAsStateWithLifecycle()

    // TODO: Implement dynamic background based on thumbnail music that are currently playing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x33000000))
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Top Bar
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

            // TODO: Only Show edit functionality if this artist id of song equal to current user id (that mean current user is the song owner)
            CircleContainer(
                size = 40.dp,
                backgroundColor = Color(0x33FFFFFF),
                onClick = { /* TODO: EDIT SONG FUNCTIONALITY */ }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.edit),
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Thumbnail
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

        // Song Info
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
                text = song.artistName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF747474),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Slider(
                value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                onValueChange = {
                    val newPos = (it * duration).toLong()
                    songViewModel.seekTo(newPos)
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

        // Similar Songs List (Bottom sheet style container)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF262626))
                .padding(horizontal = 16.dp, vertical = 21.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Previous Button
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.play_skip_back),
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { songViewModel.seekTo(0L) }
                )

                Spacer(modifier = Modifier.width(45.dp))

                CircleContainer(
                    size = 50.dp,
                    backgroundColor = yellow,
                    onClick = {
                        songViewModel.togglePlayPause()
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

                Spacer(modifier = Modifier.width(45.dp))

                // Next Button
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.play_skip_forward),
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            val nextSong = similarSongs.random()
                            onNavigateToPlay(nextSong)
                        }
                )
            }

            // Swipe up indicator / similar music header
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
                similarSongs.forEach { songItem ->
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

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showSystemUi = true)
@Composable
fun PlayMusicScreenPreview() {
    // SwaraBoxTheme {
    //     PlayMusicScreen(
    //         songViewModel = ..., // Need a mock or dummy
    //         song = SongModel.dummyList[0],
    //         similarSongs = SongModel.dummyList,
    //         onNavigateToPlay = {},
    //         onBack = {}
    //     )
    // }
}

