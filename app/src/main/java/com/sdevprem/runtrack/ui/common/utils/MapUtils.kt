package com.sdevprem.runtrack.ui.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.sdevprem.runtrack.domain.tracking.model.PathPoint
import kotlinx.coroutines.delay

object MapUtils {

    fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorResId: Int,
        tint: Int? = null
    ): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        tint?.let { vectorDrawable.setTint(it) }
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    suspend fun takeSnapshot(
        map: GoogleMap,
        pathPoints: List<PathPoint>,
        onSnapshot: (Bitmap) -> Unit,
        snapshotSideLength: Float,
        mapLoaded: Boolean
    ) {
        if (!mapLoaded) return

        val boundsBuilder = LatLngBounds.Builder()
        var hasPoints = false

        pathPoints.forEach {
            if (it is PathPoint.LocationPoint) {
                boundsBuilder.include(LatLng(it.locationInfo.latitude, it.locationInfo.longitude))
                hasPoints = true
            }
        }

        if (hasPoints) {
            val bounds = boundsBuilder.build()
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
            
            // Wait a bit for the camera to settle and tiles to load
            delay(1000)

            map.snapshot { bitmap ->
                if (bitmap != null) {
                   onSnapshot(bitmap)
                }
            }
        }
    }
}
