package com.pamt.swarabox.ui.theme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.Locale
import androidx.core.graphics.scale
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import java.net.UnknownHostException

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

/**
 * Converts exceptions from Supabase / network into user-friendly error messages
 * in English, while preserving the original technical meaning.
 *
 * Usage in ViewModel:
 * ```kotlin
 * } catch (e: Exception) {
 *     _uiState.value = SomeUiState.Error(e.toUserFriendlyMessage())
 * }
 * ```
 */
fun Throwable.convertMessage(): String {
    return when (this) {
        is UnauthorizedRestException -> {
            val raw = message?.lowercase() ?: ""
            when {
                raw.contains("invalid login credentials") ||
                        raw.contains("invalid email or password") ->
                    "The email or password you entered is incorrect. Please try again."

                raw.contains("token is expired") ||
                        raw.contains("jwt expired") ->
                    "Your session has expired. Please sign in again."

                raw.contains("user not found") ->
                    "Account not found. Make sure the email address is registered."

                else -> "You don't have access. Please sign in again."
            }
        }

        is BadRequestRestException -> {
            val raw = message?.lowercase() ?: ""
            when {
                raw.contains("user already registered") ||
                        raw.contains("email address already in use") ||
                        raw.contains("already been registered") ->
                    "This email is already registered. Use a different email or sign in to the existing account."

                raw.contains("password should be at least") ||
                        raw.contains("weak password") ->
                    "Password is too weak. Use at least 6 characters."

                raw.contains("invalid email") ->
                    "Invalid email format. Please double-check your email address."

                raw.contains("signup is disabled") ->
                    "New account registration is currently disabled. Please try again later."

                raw.contains("email rate limit exceeded") ||
                        raw.contains("too many requests") ->
                    "Too many attempts. Please wait a moment before trying again."

                raw.contains("violates") && raw.contains("constraint") ->
                    "The submitted data is invalid or a duplicate already exists."

                raw.contains("null value") ->
                    "Some required fields are empty. Please review your form."

                else -> "Invalid request. Please review the data you entered."
            }
        }

        is NotFoundRestException -> {
            val raw = message?.lowercase() ?: ""
            when {
                raw.contains("user") ->
                    "User account not found."

                raw.contains("song") || raw.contains("lagu") ->
                    "Song not found."

                else -> "The requested data was not found."
            }
        }

        is RestException -> {
            val raw = message?.lowercase() ?: ""
            when {
                raw.contains("permission denied") ||
                        raw.contains("row-level security") ||
                        raw.contains("insufficient privilege") ->
                    "You don't have permission to perform this action."

                raw.contains("duplicate key") ||
                        raw.contains("unique constraint") ->
                    "Data already exists. Cannot save a duplicate."

                raw.contains("foreign key") ->
                    "Related data not found. Make sure the data reference is correct."

                raw.contains("not-null constraint") ||
                        raw.contains("null value in column") ->
                    "A required column is empty."

                raw.contains("check constraint") ->
                    "The value you entered does not meet the required conditions."

                raw.contains("storage") || raw.contains("bucket") ->
                    "There was a problem uploading the file. Make sure the file size does not exceed the limit."

                raw.contains("rate limit") ->
                    "Too many requests. Please wait a moment and try again."

                else -> "A server error occurred. Please try again later."
            }
        }



        is UnknownHostException ->
            "Unable to connect to the internet. Please check your network connection."

        is ConnectTimeoutException ->
            "The connection took too long. Please check your network and try again."

        is SocketTimeoutException ->
            "The server did not respond in time. Please try again later."

        is HttpRequestException -> {
            val cause = cause
            when (cause) {
                is UnknownHostException -> "Unable to connect to the internet. Please check your network connection."
                is ConnectTimeoutException, is SocketTimeoutException -> "Connection timed out. Please check your network and try again."
                else -> "A network error occurred. Please check your internet connection."
            }
        }

        else -> {
            val raw = message?.lowercase() ?: ""
            when {
                raw.contains("object not found") ->
                    "File not found on the server."

                raw.contains("payload too large") ||
                        raw.contains("file size") ->
                    "File size is too large. Please use a smaller file."

                raw.contains("mime type") ||
                        raw.contains("invalid file type") ->
                    "File type is not supported. Please use a compatible format."


                raw.contains("unable to resolve host") ||
                        raw.contains("failed to connect") ||
                        raw.contains("no internet") ->
                    "Unable to connect to the internet. Please check your network connection."

                raw.contains("timeout") ->
                    "Connection timed out. Please try again later."

                raw.contains("ssl") || raw.contains("certificate") ->
                    "A connection security error occurred. Please try again later."


                raw.contains("session") ->
                    "Your session has expired. Please sign in again."

                else -> message ?: "An unknown error occurred. Please try again later."
            }
        }
    }
}
