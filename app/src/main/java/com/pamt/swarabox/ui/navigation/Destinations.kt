package com.pamt.swarabox.ui.navigation

import com.pamt.swarabox.data.model.SongModel
import com.pamt.swarabox.data.model.UserModel
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val isForcedRefresh: Boolean = false
)

@Serializable
object Upload

@Serializable
data class PlayMusic(
    val song: SongModel
)

@Serializable
object Landing

@Serializable
object Login

@Serializable
object RegisterName

@Serializable
object RegisterEmailPassword

@Serializable
object Profile

@Serializable
object About

@Serializable
data class EditProfile(
    val currentUser: UserModel
)

@Serializable
data class EditSong(
    val currentSong: SongModel
)