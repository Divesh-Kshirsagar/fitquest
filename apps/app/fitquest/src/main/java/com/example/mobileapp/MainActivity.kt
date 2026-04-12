package com.example.mobileapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.mobileapp.features.capture.CaptureScreenModel
import com.example.mobileapp.ui.capture.CurrentRunScreen
import com.example.mobileapp.ui.theme.MobileAppTheme
import com.uber.h3core.H3Core
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val captureScreenModel: CaptureScreenModel by inject()
    private val h3CoreResult: Result<H3Core> by inject()
    private val h3Core: H3Core?
        get() = h3CoreResult.getOrNull()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureRuntimePermissions()
        enableEdgeToEdge()
        setContent {
            MobileAppTheme {
                CurrentRunScreen(
                    screenModel = captureScreenModel,
                    h3Core = h3Core
                )
            }
        }
    }

    private fun ensureRuntimePermissions() {
        val permissions = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }
}