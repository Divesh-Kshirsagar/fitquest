package com.example.mobileapp.ui.capture

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.mobileapp.BuildConfig
import com.example.mobileapp.core.permissions.PermissionManager
import com.example.mobileapp.features.capture.CaptureScreenModel
import com.example.mobileapp.features.capture.CaptureState
import org.orbitmvi.orbit.compose.collectAsState
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource

// ─────────────────────────────────────────────────────────────────────────────
// Top-level screen: permission gate → map
// ─────────────────────────────────────────────────────────────────────────────

class CurrentRunScreen : Screen {
    @SuppressLint("MissingPermission")
    @Composable
    override fun Content() {
        val modifier: Modifier = Modifier
        val screenModel = koinScreenModel<CaptureScreenModel>()
        val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(PermissionManager.hasAllPermissions(context)) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        permissionsGranted = allGranted
        if (!allGranted) {
            // If the user denied without "Don't ask again", the launcher will just
            // re-prompt next time. We mark permanently denied only when a permission
            // is fully denied (the system won't show the dialog again).
            permanentlyDenied = true
        }
    }

    if (!permissionsGranted) {
        PermissionGateScreen(
            permanentlyDenied = permanentlyDenied,
            onRequestPermissions = {
                permissionLauncher.launch(PermissionManager.REQUIRED_PERMISSIONS)
            },
            onOpenSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
        return
    }

    val state by screenModel.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    Box(modifier = modifier.fillMaxSize()) {
        CaptureMap(
            state = state,
            screenModel = screenModel,
            modifier = Modifier.fillMaxSize()
        )

        // Top Navigation Bar (Back button)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (state.isTracking) {
                        screenModel.onToggleTracking()
                    }
                    navigator.pop()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            ) {
                Text("✕", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        // Live HUD card
        ElevatedCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 64.dp, end = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isTracking) "ACTIVE RUN" else "STANDBY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isTracking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = formatDuration(state.durationSeconds),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text("Steps", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${state.sessionSteps}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Distance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("%.2f km", state.distanceMeters / 1000.0), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Calories", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${state.caloriesBurned} kcal", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Hexes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("+${state.sessionCapturedHexes.size}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Text(
                    text = "Current Hex: ${state.currentHexId?.take(8) ?: "Scanning..."}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isTracking) {
                FilledTonalButton(
                    onClick = screenModel::onTogglePause,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(if (state.isPaused) "Resume" else "Pause")
                }
            }

            ExtendedFloatingActionButton(
                onClick = screenModel::onToggleTracking,
                containerColor = if (state.isTracking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (state.isTracking) "Stop & Finish" else "Start Capture",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Post-Run Victory Summary Dialog
        if (state.showSummaryDialog && state.latestCompletedSession != null) {
            val session = state.latestCompletedSession!!
            AlertDialog(
                onDismissRequest = {
                    screenModel.dismissSummaryDialog()
                    navigator.pop()
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🎉 Run Completed!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                        Text("Session territory saved", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("+${session.xpEarned} XP Earned", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("${session.capturedHexCount} Hexagons Conquered", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Steps", style = MaterialTheme.typography.bodyMedium)
                            Text("${session.totalSteps}", fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Distance Covered", style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%.2f km", session.distanceMeters / 1000.0), fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Duration", style = MaterialTheme.typography.bodyMedium)
                            Text(formatDuration(session.durationSeconds), fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Active Calories", style = MaterialTheme.typography.bodyMedium)
                            Text("${session.caloriesBurned} kcal", fontWeight = FontWeight.Bold)
                        }

                        if (state.unlockedAchievements.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🏆 Achievements Unlocked:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            state.unlockedAchievements.forEach { ach ->
                                Text("• ${ach.title}: ${ach.description}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            screenModel.dismissSummaryDialog()
                            navigator.pop()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Hub & Stats")
                    }
                }
            )
        }
    }
}
}

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    val hours = mins / 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, mins % 60, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Permission gate UI
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PermissionGateScreen(
    permanentlyDenied: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FitQuest needs your location and step data to track which hexagons you capture.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (permanentlyDenied) {
                Text(
                    text = "Permissions were denied. Please enable them in Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onOpenSettings) {
                    Text("Open Settings")
                }
            } else {
                Button(onClick = onRequestPermissions) {
                    Text("Grant Permissions")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Map composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CaptureMap(
    state: CaptureState,
    screenModel: CaptureScreenModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Defer MapView creation so the first compose frame renders instantly,
    // avoiding the "Skipped N frames" cold-start penalty from MapLibre native init.
    var mapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        // This runs after the first frame is drawn, so the loading indicator shows immediately.
        val options = MapLibreMapOptions.createFromAttributes(context)
            .textureMode(true) // TextureView avoids BLASTBufferQueue "Can't acquire" log spam
        val view = MapView(context, options).apply {
            onCreate(null)
            getMapAsync { mapLibreMap ->
                mapLibreMap.setStyle(Style.Builder().fromUri(BuildConfig.MAPTILER_STYLE_URL)) { style ->
                    ensureHexLayers(style)
                }
                
                // Add Map Idle Listener for Backend Sync
                mapLibreMap.addOnCameraIdleListener {
                    val bounds = mapLibreMap.projection.visibleRegion.latLngBounds
                    val zoom = mapLibreMap.cameraPosition.zoom
                    screenModel.onMapIdle(
                        minLat = bounds.southWest.latitude,
                        minLng = bounds.southWest.longitude,
                        maxLat = bounds.northEast.latitude,
                        maxLng = bounds.northEast.longitude,
                        zoom = zoom
                    )
                }
            }
        }
        mapView = view
    }

    // Push pre-computed GeoJSON strings to map sources.
    // These strings are already built off the UI thread in CaptureScreenModel.
    val currentMapView = mapView

    LaunchedEffect(state.nearbyHexGeoJson, currentMapView) {
        currentMapView?.getMapAsync { map ->
            map.getStyle { style ->
                ensureHexLayers(style)
                if (state.nearbyHexGeoJson.isNotEmpty()) {
                    style.getSourceAs<GeoJsonSource>(NEARBY_HEX_SOURCE_ID)
                        ?.setGeoJson(state.nearbyHexGeoJson)
                }
            }
        }
    }

    LaunchedEffect(state.capturedHexGeoJson, currentMapView) {
        currentMapView?.getMapAsync { map ->
            map.getStyle { style ->
                ensureHexLayers(style)
                if (state.capturedHexGeoJson.isNotEmpty()) {
                    style.getSourceAs<GeoJsonSource>(HEX_SOURCE_ID)
                        ?.setGeoJson(state.capturedHexGeoJson)
                }
            }
        }
    }

    LaunchedEffect(state.currentHexGeoJson, currentMapView) {
        currentMapView?.getMapAsync { map ->
            map.getStyle { style ->
                ensureHexLayers(style)
                if (state.currentHexGeoJson.isNotEmpty()) {
                    style.getSourceAs<GeoJsonSource>(CURRENT_HEX_SOURCE_ID)
                        ?.setGeoJson(state.currentHexGeoJson)
                }
            }
        }
    }

    LaunchedEffect(state.currentLocation, currentMapView) {
        state.currentLocation?.let { loc ->
            currentMapView?.getMapAsync { map ->
                val position = CameraPosition.Builder()
                    .target(LatLng(loc.latitude, loc.longitude))
                    .zoom(16.0)
                    .build()
                map.easeCamera(CameraUpdateFactory.newCameraPosition(position), 300)
            }
        }
    }

    if (currentMapView != null) {
        DisposableEffect(lifecycle, currentMapView) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> currentMapView.onStart()
                    Lifecycle.Event.ON_RESUME -> currentMapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> currentMapView.onPause()
                    Lifecycle.Event.ON_STOP -> currentMapView.onStop()
                    else -> Unit
                }
            }
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
                currentMapView.onDestroy()
            }
        }
    }

    Box(modifier = modifier) {
        if (currentMapView != null) {
            AndroidView(
                factory = { currentMapView },
                modifier = Modifier.fillMaxSize(),
                update = { }
            )
        } else {
            // Loading state while MapView initializes off the first frame
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading map…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (BuildConfig.MAPTILER_API_KEY.isBlank()) {
            Text(
                text = "MapTiler key missing (.env)",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(12.dp)
                    .background(Color(0xAA000000))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Map layer setup
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Idempotently adds all hex-related sources and layers.
 *
 * Layer order (bottom → top):
 *  1. Nearby hex grid outlines (subtle grey)
 *  2. Captured hex fills (green)
 *  3. Current hex fill (orange)
 */
private fun ensureHexLayers(style: Style) {
    // --- Nearby hex grid (outline only) ---
    if (style.getSource(NEARBY_HEX_SOURCE_ID) == null) {
        style.addSource(GeoJsonSource(NEARBY_HEX_SOURCE_ID))
    }
    if (style.getLayer(NEARBY_HEX_OUTLINE_LAYER_ID) == null) {
        val nearbyOutline = LineLayer(NEARBY_HEX_OUTLINE_LAYER_ID, NEARBY_HEX_SOURCE_ID)
            .withProperties(
                lineColor(AndroidColor.parseColor("#455A64")),
                lineWidth(1.2f),
                lineOpacity(0.35f)
            )
        style.addLayer(nearbyOutline)
    }

    // --- Captured hexes (green fill) ---
    if (style.getSource(HEX_SOURCE_ID) == null) {
        style.addSource(GeoJsonSource(HEX_SOURCE_ID))
    }
    if (style.getLayer(HEX_FILL_LAYER_ID) == null) {
        val capturedHexLayer = FillLayer(HEX_FILL_LAYER_ID, HEX_SOURCE_ID).withProperties(
            fillColor(AndroidColor.parseColor("#00C853")),
            fillOpacity(0.35f)
        )
        style.addLayer(capturedHexLayer)
    }

    // --- Current hex (orange fill) ---
    if (style.getSource(CURRENT_HEX_SOURCE_ID) == null) {
        style.addSource(GeoJsonSource(CURRENT_HEX_SOURCE_ID))
    }
    if (style.getLayer(CURRENT_HEX_FILL_LAYER_ID) == null) {
        val currentHexLayer = FillLayer(CURRENT_HEX_FILL_LAYER_ID, CURRENT_HEX_SOURCE_ID)
            .withProperties(
                fillColor(AndroidColor.parseColor("#FFA500")),
                fillOpacity(0.5f)
            )
        style.addLayer(currentHexLayer)
    }
}

private const val HEX_SOURCE_ID = "captured-hex-source"
private const val HEX_FILL_LAYER_ID = "captured-hex-fill-layer"
private const val CURRENT_HEX_SOURCE_ID = "current-hex-source"
private const val CURRENT_HEX_FILL_LAYER_ID = "current-hex-fill-layer"
private const val NEARBY_HEX_SOURCE_ID = "nearby-hex-source"
private const val NEARBY_HEX_OUTLINE_LAYER_ID = "nearby-hex-outline-layer"
