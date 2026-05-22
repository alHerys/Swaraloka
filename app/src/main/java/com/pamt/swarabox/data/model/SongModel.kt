package com.pamt.swarabox.data.model

import kotlin.time.Instant

data class SongModel(
    val id: String,
    val artistId: String,
    val title: String,
    val thumbnailUrl: String,
    val songUrl: String,
    val artist: String,
    val duration: Long,
    val isActive: Boolean,
    val createdAt: Instant, // TIMESTAMPTZ in SUPABASE
) {
    companion object {
        val dummySongs = listOf(
            SongModel(
                id = "1",
                artistId = "artist_1",
                title = "Example Song",
                thumbnailUrl = "https://picsum.photos/200",
                songUrl = "https://example.com/song.mp3",
                artist = "Example Artist",
                duration = 210000L,
                isActive = true,
                createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
            ),
            SongModel(
                id = "2",
                artistId = "artist_2",
                title = "Second Track",
                thumbnailUrl = "https://picsum.photos/200",
                songUrl = "",
                artist = "Another Artist",
                duration = 180000L,
                isActive = true,
                createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
            )
        )
    }
}
