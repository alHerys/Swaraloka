package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.FeaturedCard
import com.pamt.swarabox.ui.components.LogoWidget
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.viewmodel.player.PlayerViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    songs: List<SongModel>,
    playerViewModel: PlayerViewModel,
    isRefreshing: Boolean,
    isForcedRefresh: Boolean = false,
    onRefresh: () -> Unit,
    onNavigateToPlay: (SongModel) -> Unit,
) {
    val featuredSong = songs.firstOrNull()
    val otherSongs = if (songs.size > 1) songs.drop(1) else emptyList()

    LaunchedEffect(isForcedRefresh) {
        if (isForcedRefresh) {
            playerViewModel.release()
            onRefresh()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x33000000)),
    ) {
        if (songs.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))

                        LogoWidget()

                        Spacer(modifier = Modifier.height(24.dp))

                        if (featuredSong != null) {
                            FeaturedCard(featuredSong, onClick = { onNavigateToPlay(featuredSong) })
                        }

                        Spacer(modifier = Modifier.height(34.dp))

                        Text(
                            text = "Other Songs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                items(otherSongs) { song ->
                    Box {
                        SongTile(
                            song = song,
                            onClick = { onNavigateToPlay(song) },
                        )
                    }
                }
            }
        }
    }
}