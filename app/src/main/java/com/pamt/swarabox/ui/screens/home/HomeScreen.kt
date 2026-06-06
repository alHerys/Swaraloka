package com.pamt.swarabox.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.FeaturedCard
import com.pamt.swarabox.ui.components.LogoWidget
import com.pamt.swarabox.ui.components.LottieEmpty
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.navigation.PlayMusic
import com.pamt.swarabox.ui.theme.SwaraBoxTheme
import com.pamt.swarabox.viewmodel.song.SongUiState
import com.pamt.swarabox.viewmodel.song.SongViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel,
    navController: NavController,
) {
    val songs by songViewModel.songs.collectAsStateWithLifecycle()
    val songUiState by songViewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        modifier = modifier,
        songs = songs,
        isRefreshing = songUiState is SongUiState.Loading,
        onRefresh = { songViewModel.fetchSongs() },
        onNavigateToPlay = { song ->
            navController.navigate(PlayMusic(song))
        }
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    songs: List<SongModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigateToPlay: (SongModel) -> Unit,
) {
    val featuredSong = remember(songs) { songs.randomOrNull() }
    val otherSongs = remember(songs, featuredSong) { songs.filter { it != featuredSong } }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {

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

                    if (songs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieEmpty(
                                lottieId = R.raw.empty,
                                size = 250.dp,
                                caption = "No Songs Available\nStart Upload Now",
                                fontsize = 18.sp,
                            )
                        }
                        return@Column
                    }

                    if (featuredSong != null) {
                        Spacer(modifier = Modifier.height(24.dp))
                        FeaturedCard(featuredSong, onClick = { onNavigateToPlay(featuredSong) })
                    }

                    if (otherSongs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(34.dp))
                        Text(
                            text = "Other Songs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            items(otherSongs) { song ->
                SongTile(
                    song = song,
                    onClick = { onNavigateToPlay(song) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    SwaraBoxTheme {
        HomeContent(
            modifier = Modifier,
            songs = emptyList(),
            isRefreshing = false,
            onRefresh = { TODO() },
            onNavigateToPlay = { TODO() },
        )
    }
}