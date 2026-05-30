package com.pamt.swarabox.viewmodel.home

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
