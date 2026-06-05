package com.pamt.swarabox.data.repository

import com.pamt.swarabox.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    private val supabase = SupabaseClientProvider.client

    val sessionStatus: Flow<SessionStatus> = supabase.auth.sessionStatus

    suspend fun register(fullname: String, email: String, password: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", fullname)
            }
        }
    }

    suspend fun login(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {
        supabase.auth.signOut()
    }

    suspend fun isLoggedIn(): Boolean {
        try {
            awaitAuthInitialization()
        } catch (e: Exception) { }

        return supabase.auth.currentSessionOrNull() != null
    }

    suspend fun awaitAuthInitialization() {
        supabase.auth.awaitInitialization()
    }
}