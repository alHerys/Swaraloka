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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.LogoWidget
import com.pamt.swarabox.ui.components.SongTile
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    songs: List<SongModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigateToPlay: (SongModel) -> Unit,
) {
    val featuredSong = songs.firstOrNull()
    val otherSongs = if (songs.size > 1) songs.drop(1) else emptyList()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x33000000))
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

                    Spacer(modifier = Modifier.height(24.dp))

                    featuredSong?.let {
                        FeaturedCard(it, onClick = { onNavigateToPlay(it) })
                    } ?: run {
                        // Placeholder or empty state for featured card if needed
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF262626)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No songs available", color = Color.Gray)
                        }
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

            if (otherSongs.isEmpty() && featuredSong != null) {
                item {
                    Text(
                        text = "No more songs to show",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedCard(song: SongModel, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(476.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF262626))
            .clickable { onClick() }
    ) {
        // ... (rest of FeaturedCard remains the same, but use onClick for the button too)
        // Image at the top, square-ish as per Figma (353x353)
        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(353.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Today’s for\nYou",
                fontSize = 44.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                lineHeight = 54.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Column {
                Text(
                    text = song.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "by ${song.artist}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF838383)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(43.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .width(132.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircleContainer(
                            size = 28.dp,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            onClick = onClick
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.play),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    SwaraBoxTheme {
        Scaffold(Modifier.fillMaxSize()) { innerPadding ->
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
                onNavigateToPlay = {},
                songs = SongModel.dummyList,
                isRefreshing = false,
                onRefresh = {  }
            )
        }
    }
}