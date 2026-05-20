package com.pamt.swarabox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.pamt.swarabox.ui.screens.RegisterEmailPasswordPage
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SwaraBoxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterEmailPasswordPage(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

