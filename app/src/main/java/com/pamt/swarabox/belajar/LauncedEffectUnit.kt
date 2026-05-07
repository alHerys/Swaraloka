package com.pamt.swarabox.belajar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun LaunchedEffectUnit(modifier: Modifier = Modifier) {
    var status by remember { mutableStateOf("Memulai...") }

    LaunchedEffect(Unit) {
        delay(2000)
        status = "Selesai!"
    }

    Text(
        text = status,
        modifier = modifier
    )
}

@Composable
fun LaunchedEffectKey(modifier: Modifier = Modifier) {
    var angka by rememberSaveable { mutableIntStateOf(0) }
    var keterangan by rememberSaveable {mutableStateOf("")}

    LaunchedEffect(angka) {
        keterangan = "Proses angka $angka"
        delay(1000)
        keterangan = "Angka $angka selesai!"
    }

    Column(modifier) {
        Text(text = keterangan)
        Button(onClick = {angka++}) {
            Text("Tambah Angka")
        }
    }
}