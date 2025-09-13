package com.bossmg.android.cameraxcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bossmg.android.cameraxcompose.nav.App
import com.bossmg.android.cameraxcompose.ui.theme.CameraXComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CameraXComposeTheme {
                App()
            }
        }
    }
}