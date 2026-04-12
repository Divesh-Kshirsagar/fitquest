package com.example.mobileapp.features.capture

import com.example.mobileapp.core.model.GeoPoint

data class CaptureState(
    val isTracking: Boolean = false,
    val currentLocation: GeoPoint? = null,
    val currentHexId: String? = null,
    val sessionSteps: Int = 0,
    val sessionCapturedHexes: List<String> = emptyList(),
    val historicalCapturedHexes: List<String> = emptyList(),
    val allCapturedHexes: List<String> = emptyList(),

    // Pre-computed GeoJSON strings for the map layers.
    // Built on Dispatchers.Default to keep the UI thread free and avoid "skipped frames".
    val capturedHexGeoJson: String = "",
    val currentHexGeoJson: String = "",
    val nearbyHexGeoJson: String = ""
)
