package com.pamt.swarabox.data.repository

import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.SongModel
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.util.UUID

class SongRepository {
    private val supabase = SupabaseClientProvider.client

    companion object {
        private var cachedSongs: List<SongModel> = emptyList()
    }

    suspend fun uploadAudio(userId: String, audioBytes: ByteArray): String {
        val fileName = "audio_${userId}_${UUID.randomUUID()}.mp3"
        val bucket = supabase.storage.from("lagu")
        bucket.upload(fileName, audioBytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

    suspend fun uploadThumbnail(userId: String, imageBytes: ByteArray): String {
        val fileName = "thumb_${userId}_${UUID.randomUUID()}.png"
        val bucket = supabase.storage.from("gambar")
        bucket.upload(fileName, imageBytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

    suspend fun insertSong(song: SongModel) {
        supabase.from("song").insert(song)
    }

    suspend fun fetchAllSongs(forceRefresh: Boolean = false): List<SongModel> {
        if (forceRefresh || cachedSongs.isEmpty()) {
            cachedSongs = supabase.postgrest.rpc("fetch_all_active_song").decodeList<SongModel>()
        }
        return cachedSongs
    }

    suspend fun fetchSongsByArtist(artistId: String, forceRefresh: Boolean = false): List<SongModel> {
        if (forceRefresh || cachedSongs.isEmpty()) {
            fetchAllSongs(forceRefresh = true)
        }
        return cachedSongs.filter { it.artistId == artistId }
    }
}
