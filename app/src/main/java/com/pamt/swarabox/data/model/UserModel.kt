package com.pamt.swarabox.data.model

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
            userId = "1",
            name = "Alvianto Hery Sarborn",
            email = "john.doe@example.com",
            avatarUrl = null,
            createdAt = Clock.System.now()
        )

        val UserModelNavType = object : NavType<UserModel>(isNullableAllowed = false) {
            override fun get(bundle: Bundle, key: String): UserModel? {
                return bundle.getString(key)?.let { Json.decodeFromString(it) }
            }

            override fun parseValue(value: String): UserModel {
                return Json.decodeFromString(Uri.decode(value))
            }

            override fun put(bundle: Bundle, key: String, value: UserModel) {
                bundle.putString(key, Json.encodeToString(value))
            }

            override fun serializeAsValue(value: UserModel): String {
                return Uri.encode(Json.encodeToString(value))
            }
        }
    }
}