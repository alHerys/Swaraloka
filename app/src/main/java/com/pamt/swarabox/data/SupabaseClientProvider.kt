package com.pamt.swarabox.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://qovvdblapkgxpuoavdnw.supabase.co",
        supabaseKey = "sb_publishable_XS5CYJXmBpA4JW_0lambiw_-jSMhuwi"
    ) {
        install(Auth)
        install(Postgrest)
    }
}