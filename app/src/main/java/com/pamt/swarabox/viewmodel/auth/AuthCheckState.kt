package com.pamt.swarabox.viewmodel.auth

sealed class AuthCheckState {
    object Checking : AuthCheckState()
    object Authenticated : AuthCheckState()
    object NotAuthenticated : AuthCheckState()
}