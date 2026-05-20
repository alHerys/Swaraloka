package com.pamt.swarabox.ui

sealed class Screen(val route: String) {
    object Landing: Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}