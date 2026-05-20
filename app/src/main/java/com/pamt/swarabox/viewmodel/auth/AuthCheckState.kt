package com.example.projectpamt.viewmodel.auth

sealed class AuthCheckState {
    object Checking : AuthCheckState()
    object Authenticated : AuthCheckState()
    object NotAuthenticated : AuthCheckState()
}