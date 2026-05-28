package com.pamt.swarabox.data.model

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Instant

@Serializable
data class SongModel(
    @SerialName("song_id")
    val id: String? = null,
    @SerialName("artist_id")
    val artistId: String,
    val title: String,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String,
    @SerialName("song_url")
    val songUrl: String,
    val artist: String? = null,
    @SerialName("song_duration")
    val songDuration: Int,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: Instant? = null, // TIMESTAMPTZ in SUPABASE
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
                songDuration = 210,
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
                songDuration = 180,
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
                songDuration = 240,
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
