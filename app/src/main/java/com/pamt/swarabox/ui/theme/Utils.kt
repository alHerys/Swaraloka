package com.pamt.swarabox.ui.theme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.Locale
import androidx.core.graphics.scale

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

fun ByteArray.compress(maxSize: Int = 512, quality: Int = 75): ByteArray {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size) ?: return this
    val scale = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height, 1f)
    val scaled = bitmap.scale((bitmap.width * scale).toInt(), (bitmap.height * scale).toInt())
    return ByteArrayOutputStream().also { scaled.compress(Bitmap.CompressFormat.JPEG, quality, it) }.toByteArray()
}