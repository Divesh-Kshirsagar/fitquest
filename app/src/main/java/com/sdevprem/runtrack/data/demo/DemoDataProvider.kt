package com.sdevprem.runtrack.data.demo

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.domain.model.ColoredPolygon
import com.sdevprem.runtrack.domain.model.CurrentRunStateWithCalories
import com.sdevprem.runtrack.domain.tracking.model.CurrentRunState
import com.sdevprem.runtrack.domain.tracking.model.LocationInfo
import com.sdevprem.runtrack.domain.tracking.model.PathPoint
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Singleton
class DemoDataProvider @Inject constructor() {

    // Central Park, NY coordinates for demo
    private val demoCenterLat = 40.785091
    private val demoCenterLng = -73.968285

    val demoRunStateWithCalories: CurrentRunStateWithCalories by lazy {
        generateDemoCurrentRunState()
    }

    val demoH3Polygons: List<ColoredPolygon> by lazy {
        generateDemoH3Polygons()
    }

    fun getDemoRuns(): List<Run> {
        val runs = mutableListOf<Run>()
        val calendar = Calendar.getInstance()

        // Create a dummy 1x1 bitmap for demo purposes to satisfy non-nullable requirement
        val dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // Generate 10 runs over the last 20 days
        for (i in 0 until 10) {
            calendar.add(Calendar.DAY_OF_YEAR, -2)
            val distance = Random.nextDouble(2000.0, 8000.0).toFloat()
            val duration = (distance * Random.nextDouble(0.004, 0.006)).toLong() // Approx 4-6 min/km
            val calories = (distance * 0.06).toInt()

            runs.add(
                Run(
                    img = dummyBitmap, 
                    timestamp = calendar.time,
                    avgSpeedInKMH = (distance / 1000f) / (duration / 3600000f),
                    distanceInMeters = distance.toInt(),
                    durationInMillis = duration,
                    caloriesBurned = calories
                )
            )
        }

        // One long run
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        runs.add(
            Run(
                img = dummyBitmap,
                timestamp = calendar.time,
                avgSpeedInKMH = 10.5f,
                distanceInMeters = 12500,
                durationInMillis = 3900000, // ~65 mins
                caloriesBurned = 950
            )
        )

        return runs.sortedByDescending { it.timestamp }
    }

    private fun generateDemoCurrentRunState(): CurrentRunStateWithCalories {
        val pathPoints = mutableListOf<PathPoint>()
        // Generate a simple loop path in Central Park
        val center = LatLng(demoCenterLat, demoCenterLng)
        val radius = 0.005
        for (i in 0..100) {
            val angle = 2.0 * Math.PI * i / 100
            val lat = center.latitude + radius * cos(angle)
            val lng = center.longitude + radius * sin(angle)
            pathPoints.add(
                PathPoint.LocationPoint(
                    LocationInfo(lat, lng)
                )
            )
        }

        return CurrentRunStateWithCalories(
            currentRunState = CurrentRunState(
                distanceInMeters = 5200,
                speedInKMH = 0f, // Stopped
                isTracking = false,
                pathPoints = pathPoints
            ),
            caloriesBurnt = 350
        )
    }

    private fun generateDemoH3Polygons(): List<ColoredPolygon> {
        val polygons = mutableListOf<ColoredPolygon>()
        val center = LatLng(demoCenterLat, demoCenterLng)
        val hexCount = 60
        val hexSize = 0.0015

        for (i in 0 until hexCount) {
            // Spiral out or random scattering logic, simple random for demo
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val dist = Random.nextDouble(0.0, 0.01) // random distance from center
            val hexCenterLat = center.latitude + dist * cos(angle)
            val hexCenterLng = center.longitude + dist * sin(angle)
            
            // Generate hex points
            val points = mutableListOf<LatLng>()
            for (j in 0 until 6) {
                val hexAngle = 2.0 * Math.PI * j / 6
                points.add(
                    LatLng(
                        hexCenterLat + hexSize * cos(hexAngle),
                        hexCenterLng + hexSize * sin(hexAngle)
                    )
                )
            }

            // Assign random realistic color based on "visit count" (simulated)
            val color = when (Random.nextInt(4)) {
                0 -> 0x80F44336L // Red (High traffic)
                1 -> 0x80FFEB3BL // Yellow
                2 -> 0x804CAF50L // Green
                else -> 0x802196F3L // Blue (Low traffic)
            }

            polygons.add(
                ColoredPolygon(
                    id = i.toLong(),
                    points = points,
                    color = color
                )
            )
        }
        return polygons
    }
}
