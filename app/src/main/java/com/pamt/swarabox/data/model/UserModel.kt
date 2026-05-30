package com.pamt.swarabox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class UserModel(
    @SerialName("user_id") val userId: String,
    val name: String,
    val email: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("created_at") val createdAt: Instant,
) {
    companion object {
        val dummy = UserModel(
            name = "Alvianto Hery Sarborn",
            email = "john.doe@example.com",
            userId = "1",
            avatarUrl = null,
            createdAt = Clock.System.now()
        )
    }
}
