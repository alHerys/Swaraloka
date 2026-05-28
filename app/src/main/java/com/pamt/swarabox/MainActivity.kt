package com.pamt.swarabox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pamt.swarabox.ui.navigation.AppNavigation
import com.pamt.swarabox.ui.theme.SwaraBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwaraBoxTheme {
                AppNavigation()
            }
        }
    }
}

