package com.pamt.swarabox.belajar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CounterScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterViewModel = viewModel()
) {
    val angka by viewModel.angka.collectAsStateWithLifecycle()

    Column(modifier) {
        Text(text = "Angka: $angka")
        Button(
            onClick = { viewModel.tambahAngka() }
        ) {
            Text("Tambah")
        }
    }
}