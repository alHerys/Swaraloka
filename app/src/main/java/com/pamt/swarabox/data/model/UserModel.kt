package com.pamt.swarabox.data.model

import kotlin.time.Instant

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val createdAt: Instant,
)
