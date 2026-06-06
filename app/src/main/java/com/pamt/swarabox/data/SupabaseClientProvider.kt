package com.pamt.swarabox.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://nwrrjkxknppfunexzipr.supabase.co",
        supabaseKey = "sb_publishable_Vv8gOa2zHEubqMEgX1TJDQ_mAZpwqfF"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}