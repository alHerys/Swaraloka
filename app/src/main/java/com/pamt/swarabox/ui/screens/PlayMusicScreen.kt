package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.theme.SwaraBoxTheme
import com.pamt.swarabox.ui.theme.yellow

@Composable
fun PlayMusicScreen(
    song: SongModel,
    onBack: () -> Unit,
    onNavigateToPlay: (SongModel) -> Unit,
    similarSongs: List<SongModel>,
) {
    // TODO: Implement dynamic background based on thumbnail
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
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

            CircleContainer(
                size = 40.dp,
                backgroundColor = Color(0x33FFFFFF),
                onClick = { /* TODO */ }
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
                text = song.artist,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF747474),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Progress Bar
        // TODO: Implement audio wave progress bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Slider(
                value = 0.45f,
                onValueChange = {},
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color(0xFFC1FF93),
                    inactiveTrackColor = Color(0xFF333333)
                ),
                modifier = Modifier.height(4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "01:30", fontSize = 12.sp, color = Color.White)
                Text(text = "03:30", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Previous Button (dummy using rotated play)
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.play_skip_back),
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { /* TODO */ }
                    // Rotate 180 would be better but if it's skip back it usually has a bar.
                    // For now let's just use it as is for dummy.
            )
            
            Spacer(modifier = Modifier.width(45.dp))
            
            CircleContainer(
                size = 50.dp,
                backgroundColor = yellow,
                onClick = { /* TODO */ }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.play),
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(45.dp))

            // Next Button (dummy using play)
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.play_skip_forward),
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Swipe up indicator / similar music header
        Text(
            text = "swipe up to find similar music",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color(0xFF747474),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Similar Songs List (Bottom sheet style container)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF262626))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            similarSongs.forEach { song ->
                SongTile(
                    song = song,
                    containerColor = Color(0xFF1A1A1A),
                    onClick = { onNavigateToPlay(song) }
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PlayMusicScreenPreview() {
    SwaraBoxTheme {
        PlayMusicScreen(
            onNavigateToPlay = { },
            song = SongModel.dummyList[0],
            onBack = {  },
            similarSongs = SongModel.dummyList
        )
    }
}
