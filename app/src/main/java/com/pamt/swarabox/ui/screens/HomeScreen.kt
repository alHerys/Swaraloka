package com.pamt.swarabox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.FeaturedCard
import com.pamt.swarabox.ui.components.LogoWidget
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.navigation.PlayMusic
import com.pamt.swarabox.ui.theme.SwaraBoxTheme
import com.pamt.swarabox.viewmodel.player.PlayerViewModel
import com.pamt.swarabox.viewmodel.song.SongUiState
import com.pamt.swarabox.viewmodel.song.SongViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    isForcedRefresh: Boolean = false,
    songViewModel: SongViewModel,
    navController: NavController,
) {
    val songs by songViewModel.songs.collectAsStateWithLifecycle()
    val songUiState by songViewModel.uiState.collectAsStateWithLifecycle()

    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.empty)
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(isForcedRefresh) {
        if (isForcedRefresh) {
            playerViewModel.release()
            songViewModel.fetchSongs()
        }
    }

    HomeContent(
        modifier = modifier,
        songs = songs,
        lottieComposition = lottieComposition,
        lottieProgress = lottieProgress,
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
    lottieComposition: LottieComposition?,
    lottieProgress: Float,
) {
    val featuredSong = songs.firstOrNull()
    val otherSongs = if (songs.size > 1) songs.drop(1) else emptyList()

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
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                LottieAnimation(
                                    composition = lottieComposition,
                                    progress = { lottieProgress },
                                    modifier = Modifier.size(250.dp)
                                )
                                Spacer(modifier = Modifier.height(34.dp))
                                Text(
                                    text = "No Songs Available\nStart Upload Now",
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                        return@Column
                    }

                    if (otherSongs.size > 1 && featuredSong != null) {
                        Spacer(modifier = Modifier.height(24.dp))
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
            lottieComposition = null,
            lottieProgress = 0F
        )
    }
}