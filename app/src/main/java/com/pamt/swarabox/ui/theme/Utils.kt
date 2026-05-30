package com.pamt.swarabox.ui.theme

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.pamt.swarabox.viewmodel.auth.AuthUiState
import kotlinx.coroutines.launch
import java.util.Locale

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
