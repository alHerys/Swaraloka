package com.pamt.swarabox.data.model

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Instant

@Serializable
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
        val dummyList = listOf(
            SongModel(
                id = "1",
                artistId = "artist_1",
                title = "Example Song",
                thumbnailUrl = "https://picsum.photos/300",
                songUrl = "https://samplelib.com/mp3/sample-speech-5m.mp3",
                artist = "Example Artist",
                duration = 210000L,
                isActive = true,
                createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
            ),
            SongModel(
                id = "2",
                artistId = "artist_2",
                title = "Second Track",
                thumbnailUrl = "https://picsum.photos/100",
                songUrl = "https://samplelib.com/mp3/sample-55s.mp3",
                artist = "Another Artist",
                duration = 180000L,
                isActive = true,
                createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
            ),
            SongModel(
                id = "3",
                artistId = "artist_3",
                title = "Third Melody",
                thumbnailUrl = "https://picsum.photos/200",
                songUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                artist = "Melody Maker",
                duration = 240000L,
                isActive = true,
                createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
            )
        )
    }
}

val SongModelNavType = object : NavType<SongModel>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): SongModel? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): SongModel {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: SongModel) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: SongModel): String {
        return Uri.encode(Json.encodeToString(value))
    }
}
