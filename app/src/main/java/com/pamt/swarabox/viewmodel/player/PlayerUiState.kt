package com.pamt.swarabox.viewmodel.player

sealed class PlayerUiState {
    object Idle : PlayerUiState()
    object Loading : PlayerUiState()
    object Playing : PlayerUiState()
}