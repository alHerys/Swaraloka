package com.pamt.swarabox.data.repository

import com.pamt.swarabox.data.SupabaseClientProvider
import com.pamt.swarabox.data.model.UserModel
import io.github.jan.supabase.postgrest.from

class ProfileRepository {
    private val supabase = SupabaseClientProvider.client

    // get profile from user table postgre supabase
    suspend fun getCurrentProfile(userId: String): UserModel {
        return supabase.from("user")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }.decodeSingle<UserModel>()

    }

    // Edit profile in user table postgre supabase
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
                    eq("user_id", userId)
                }
            }
    }
}
