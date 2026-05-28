package com.pamt.swarabox.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pamt.swarabox.R
import com.pamt.swarabox.ui.components.AppButton
import com.pamt.swarabox.ui.components.AppTextField
import com.pamt.swarabox.ui.components.CircleContainer
import com.pamt.swarabox.ui.components.DashedSelector
import com.pamt.swarabox.viewmodel.profile.ProfileUiState

@Composable
fun UploadScreen(
    profileUiState: ProfileUiState
) {
    var title by remember { mutableStateOf("") }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val artistId = (profileUiState as? ProfileUiState.Success)?.user?.userId ?: ""

    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAudioUri = uri
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x33000000))
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Text(
            text = "Upload New Song",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        if (selectedImageUri != null) {
            // Image Preview Area
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF262626))
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Edit Icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { selectedImageUri = null },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.cancel),
                        contentDescription = "Edit Image",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            // Image Selection Area
            DashedSelector(
                label = "Select Image",
                icon = ImageVector.vectorResource(id = R.drawable.image_upload),
                onClick = { imageLauncher.launch("image/*") }
            )
        }

        if (selectedAudioUri != null) {
            Spacer(modifier = Modifier.height(40.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                DummyWaveform()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("01:30", color = Color.White, fontSize = 12.sp)
                    Text("03:30", color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Audio Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dummy spacer or left-side icon (using weight to push it)
                    Box(modifier = Modifier.weight(1f))

                    // Center Icon
                    CircleContainer(
                        size = 50.dp,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.play),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Right-side Icon with weight to balance the center
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        IconButton(onClick = { selectedAudioUri = null }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.cancel),
                                contentDescription = "Replace Audio",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(21.dp))

            // Audio Selection Area
            DashedSelector(
                label = "Select .mp3 or .m4a",
                icon = ImageVector.vectorResource(id = R.drawable.song_upload),
                onClick = { audioLauncher.launch("audio/*") }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        AppTextField(
            value = title,
            onValueChange = { title = it },
            label = "Title",
            placeholder = "Enter song title"
        )

        Spacer(modifier = Modifier.height(48.dp))

        AppButton(
            onClick = {
                // TODO: Trigger upload with title, artistId, selectedAudioUri, and selectedImageUri
            },
            text = "Upload",
            containerColor = Color(0xFF007AFF),
            textColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.5f),
            disabledContainerColor = Color(0x33FFFFFF),
            enabled = (selectedImageUri != null && selectedAudioUri != null && title.isNotBlank()),
            modifier = Modifier.width(175.dp)
        )
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun DummyWaveform() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val barCount = 40
        repeat(barCount) { index ->
            val heightPercent = if (index % 3 == 0) 0.8f else if (index % 2 == 0) 0.4f else 0.6f
            val color = if (index < barCount / 3) Color(0xFFC1FF93) else Color(0xFFDEDEDE)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(heightPercent)
                    .clip(RoundedCornerShape(88.dp))
                    .background(color)
            )
        }
    }
}

