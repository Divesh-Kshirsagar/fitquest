package com.example.mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobileapp.features.capture.CaptureScreenModel
import com.example.mobileapp.ui.capture.CurrentRunScreen
import com.example.mobileapp.ui.theme.MobileAppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val captureScreenModel: CaptureScreenModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileAppTheme {
                CurrentRunScreen(
                    screenModel = captureScreenModel
                )
            }
        }
    }
}
