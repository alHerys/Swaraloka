package com.pamt.swarabox.data.repository

import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.UserModel
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

class ProfileRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getCurrentProfile(userId: String): UserModel {
        return supabase.from("user")
            .select {
                filter {
                    eq(
                        column = "user_id",
                        value = userId
                    )
                }
            }.decodeSingle<UserModel>()
    }

    suspend fun editUserProfile(
        userId: String,
        name: String,
        avatarUrl: String?
    ) {
        supabase.from("user")
            .update({
                set("name", name)
                set("avatar_url", avatarUrl)
            }) {
                filter {
                    eq(
                        column = "user_id",
                        value = userId
                    )
                }
            }
    }

    suspend fun uploadAvatar(userId: String, byteArray: ByteArray): String {
        val fileName = "avatar_$userId.png"
        val bucket = supabase.storage.from("gambar")
        bucket.upload(fileName, byteArray) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

}
