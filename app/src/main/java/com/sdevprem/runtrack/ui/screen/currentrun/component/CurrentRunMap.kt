package com.sdevprem.runtrack.ui.screen.currentrun.component

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.domain.tracking.model.PathPoint
import com.sdevprem.runtrack.domain.tracking.model.lastLocationPoint
import com.sdevprem.runtrack.ui.common.utils.MapUtils
import com.sdevprem.runtrack.ui.theme.RTColor
import com.sdevprem.runtrack.ui.theme.md_theme_light_primary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.sdevprem.runtrack.domain.model.ColoredPolygon
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapEffect
import com.sdevprem.runtrack.domain.tracking.model.firstLocationPoint
import com.google.maps.android.compose.Polygon

@Composable
fun Map(
    modifier: Modifier = Modifier,
    pathPoints: List<PathPoint>,
    h3Polygons: List<ColoredPolygon>,
    isRunningFinished: Boolean,
    onSnapshot: (Bitmap) -> Unit,
) {
    var mapSize by remember { mutableStateOf(Size(0f, 0f)) }
    var isMapLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                val rect = it.boundsInRoot()
                mapSize = rect.size
            }
    ) {
        ShowMapLoadingProgressBar(!isMapLoaded)
        Map(
            pathPoints = pathPoints,
            h3Polygons = h3Polygons,
            isRunningFinished = isRunningFinished,
            mapSize = mapSize,
            onMapLoaded = { isMapLoaded = true },
            onSnapshot = onSnapshot
        )
    }
}

@Composable
private fun Map(
    pathPoints: List<PathPoint>,
    h3Polygons: List<ColoredPolygon>,
    isRunningFinished: Boolean,
    mapSize: Size,
    onMapLoaded: () -> Unit,
    onSnapshot: (Bitmap) -> Unit,
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    
    val lastLocationPoint = pathPoints.lastLocationPoint()
    
    // Animate camera to latest location
    LaunchedEffect(lastLocationPoint) {
        lastLocationPoint?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        LatLng(it.locationInfo.latitude, it.locationInfo.longitude),
                        17f
                    )
                )
            )
        }
    }

    // Google Map Composable
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false), // We draw our own location marker
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = false
        ),
        onMapLoaded = onMapLoaded
    ) {
        // Draw H3 Polygons
        h3Polygons.forEach { polygon ->
            androidx.compose.runtime.key(polygon.id) {
                Polygon(
                    points = polygon.points,
                    fillColor = Color(polygon.color),
                    strokeColor = Color(polygon.color).copy(alpha = 0.8f),
                    strokeWidth = 2f
                )
            }
        }

        // Draw Path
        if (pathPoints.isNotEmpty()) {
            val latLngs = mutableListOf<LatLng>()
            pathPoints.forEach { point ->
                 if (point is PathPoint.LocationPoint) {
                     latLngs.add(LatLng(point.locationInfo.latitude, point.locationInfo.longitude))
                 } else if (point is PathPoint.EmptyLocationPoint) {
                     // Draw current segment
                     if (latLngs.isNotEmpty()) {
                         Polyline(
                             points = latLngs.toList(),
                             color = md_theme_light_primary,
                             width = 10f
                         )
                         latLngs.clear()
                     }
                 }
            }
            // Draw remaining segment
            if (latLngs.isNotEmpty()) {
                Polyline(
                    points = latLngs.toList(),
                    color = md_theme_light_primary,
                    width = 10f
                )
            }
        }

        // Draw Start Marker
        pathPoints.firstLocationPoint()?.let { point ->
            Marker(
                state = MarkerState(position = LatLng(point.locationInfo.latitude, point.locationInfo.longitude)),
                icon = MapUtils.bitmapDescriptorFromVector(
                    context,
                    R.drawable.ic_location_marker,
                    RTColor.CHATEAU_GREEN.toArgb()
                ),
                anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.8f),
                title = "Start"
            )
        }

        // Draw End/Current Marker
        lastLocationPoint?.let { point ->
             if (isRunningFinished) {
                 Marker(
                     state = MarkerState(position = LatLng(point.locationInfo.latitude, point.locationInfo.longitude)),
                     icon = MapUtils.bitmapDescriptorFromVector(
                         context,
                         R.drawable.ic_location_marker,
                         Color.Red.toArgb()
                     ),
                     anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.8f),
                     title = "Finish"
                 )
             } else {
                 val largeIcon = MapUtils.bitmapDescriptorFromVector(
                     context,
                     R.drawable.ic_circle,
                     md_theme_light_primary.copy(alpha = 0.4f).toArgb()
                 )
                 val smallIcon = MapUtils.bitmapDescriptorFromVector(
                     context,
                     R.drawable.ic_circle,
                     md_theme_light_primary.toArgb()
                 )
                 
                 // Large outer circle
                 Marker(
                     state = MarkerState(position = LatLng(point.locationInfo.latitude, point.locationInfo.longitude)),
                     icon = largeIcon,
                     anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
                 )
                 // Small inner circle
                 Marker(
                     state = MarkerState(position = LatLng(point.locationInfo.latitude, point.locationInfo.longitude)),
                     icon = smallIcon,
                     anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
                 )
             }
            }

        // Capture map instance for snapshot
        // com.google.maps.android.compose.MapEffect(isRunningFinished) { map ->
        //     if (isRunningFinished) {
        //         MapUtils.takeSnapshot(
        //             map,
        //             pathPoints,
        //             onSnapshot,
        //             mapSize.width / 2f,
        //             true
        //         )
        //     }
        // }
    }
}

@Composable
private fun ShowMapLoadingProgressBar(
    visible: Boolean = false
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = visible,
        enter = EnterTransition.None,
        exit = fadeOut(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize()
        )
    }
}