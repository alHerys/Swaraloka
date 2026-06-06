package com.pamt.swarabox.data.repository

import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.ui.theme.compress
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.util.UUID

class SongRepository {
    private val supabase = SupabaseClientProvider.client

    companion object {
        private var cachedSongs: List<SongModel> = emptyList()
    }

    suspend fun insertSong(
        title: String,
        artistId: String,
        audioBytes: ByteArray,
        imageBytes: ByteArray,
        duration: Int
    ) {
        val audioUrl = uploadOrReplaceAudio(
            userId = artistId,
            audioBytes = audioBytes
        )

        val thumbnailUrl = uploadOrReplaceThumbnail(
            userId = artistId,
            imageBytes = imageBytes
        )

        val song = SongModel(
            artistId = artistId,
            title = title,
            songUrl = audioUrl,
            thumbnailUrl = thumbnailUrl,
            songDuration = duration
        )

        supabase.from("song").insert(song)

        cachedSongs = emptyList()
    }

    suspend fun fetchAllSongs(forceRefresh: Boolean = false): List<SongModel> {
        if (forceRefresh || cachedSongs.isEmpty()) {
            cachedSongs = supabase.postgrest.rpc("fetch_all_active_song").decodeList<SongModel>()
        }
        return cachedSongs
    }

    suspend fun fetchSongsByArtist(
        artistId: String,
        forceRefresh: Boolean = false
    ): List<SongModel> {
        if (forceRefresh || cachedSongs.isEmpty()) {
            fetchAllSongs(forceRefresh = true)
        }
        return cachedSongs.filter { it.artistId == artistId }
    }

    suspend fun updateSong(
        songId: String,
        artistId: String,
        title: String?,
        oldSongUrl: String,
        oldThumbnailUrl: String,
        audioBytes: ByteArray?,
        imageBytes: ByteArray?,
        duration: Int?
    ) {
        val songUrl = audioBytes?.let {
            uploadOrReplaceAudio(
                userId = artistId,
                audioBytes = it,
                oldSongUrl = oldSongUrl
            )
        }
        val thumbnailUrl = imageBytes?.let {
            uploadOrReplaceThumbnail(
                userId = artistId,
                imageBytes = it,
                oldThumbnailUrl = oldThumbnailUrl
            )
        }
        supabase.from("song").update({
            title?.let { set("title", it) }
            thumbnailUrl?.let { set("thumbnail_url", it) }
            songUrl?.let {
                set("song_url", it)
                set("song_duration", duration)
            }
        }) {
            filter {
                eq("song_id", songId)
            }
        }

        cachedSongs = emptyList()
    }

    suspend fun deleteSong(
        songId: String,
        songUrl: String,
        thumbnailUrl: String
    ) {
        supabase.from("song").update({
            set("is_active", false)
        }) {
            filter { eq("song_id", songId) }
        }

        deleteFile("lagu", songUrl)
        deleteFile("gambar", thumbnailUrl)

        cachedSongs = emptyList()
    }

    private suspend fun uploadOrReplaceAudio(
        userId: String,
        oldSongUrl: String? = null,
        audioBytes: ByteArray
    ): String {
        val fileName = oldSongUrl?.substringAfter("/lagu/")
            ?: "audio_${userId}_${UUID.randomUUID()}.mp3"
        val bucket = supabase.storage.from("lagu")
        bucket.upload(fileName, audioBytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

    private suspend fun uploadOrReplaceThumbnail(
        userId: String,
        oldThumbnailUrl: String? = null,
        imageBytes: ByteArray
    ): String {
        val filePath = oldThumbnailUrl?.substringAfter("/gambar/")
            ?: "thumb_${userId}_${UUID.randomUUID()}.png"
        val bucket = supabase.storage.from("gambar")
        bucket.upload(filePath, imageBytes.compress(maxSize = 400)) {
            upsert = true
        }

        val baseUrl = bucket.publicUrl(filePath)
        return "$baseUrl?v=${System.currentTimeMillis()}"
    }

    private suspend fun deleteFile(
        bucket: String,
        fileUrl: String
    ) {
        val filePath = fileUrl.substringAfter("/${bucket}/")
        supabase.storage.from(bucket).delete(filePath)
    }
}
