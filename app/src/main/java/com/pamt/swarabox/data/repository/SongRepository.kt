package com.pamt.swarabox.data.repository

import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.SongModel
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.util.UUID

class SongRepository {
    private val supabase = SupabaseClientProvider.client

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

    suspend fun insertSong(
        artistId: String,
        title: String,
        songUrl: String,
        thumbnailUrl: String,
        duration: Int
    ) {
        val song = SongModel(
            artistId = artistId,
            title = title,
            songUrl = songUrl,
            thumbnailUrl = thumbnailUrl,
            songDuration = duration
        )
        
        supabase.from("song").insert(song)
    }
}
