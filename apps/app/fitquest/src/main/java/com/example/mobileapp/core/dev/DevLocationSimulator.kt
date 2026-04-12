package com.example.mobileapp.core.dev

import android.location.Location
import com.example.mobileapp.core.model.GeoPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Emits a sequence of [Location] objects along a predefined walk path.
 * Used for testing hex transitions on emulators or without physically walking.
 *
 * TODO(TESTING): Replace [DEFAULT_WALK_PATH] with a path around your test area.
 *  You can generate paths at https://geojson.io — draw a line, copy the coordinates.
 */
class DevLocationSimulator {

    companion object {
        /**
         * Default walk path: a small loop in central Delhi (Connaught Place area).
         * TODO(TESTING): Change this to a location near you for easier testing.
         */
        val DEFAULT_WALK_PATH = listOf(
            GeoPoint(28.6315, 77.2167),
            GeoPoint(28.6318, 77.2172),
            GeoPoint(28.6322, 77.2178),
            GeoPoint(28.6326, 77.2183),
            GeoPoint(28.6330, 77.2188),
            GeoPoint(28.6334, 77.2183),
            GeoPoint(28.6330, 77.2178),
            GeoPoint(28.6326, 77.2172),
            GeoPoint(28.6322, 77.2167),
            GeoPoint(28.6318, 77.2162),
            GeoPoint(28.6315, 77.2167)  // loop back to start
        )
    }

    /**
     * Simulates walking along [path], emitting one [Location] every [intervalMs] milliseconds.
     * Loops forever until the collecting coroutine is cancelled.
     */
    fun simulateWalk(
        path: List<GeoPoint> = DEFAULT_WALK_PATH,
        intervalMs: Long = 2_000L
    ): Flow<Location> = flow {
        var index = 0
        while (true) {
            val point = path[index % path.size]
            val location = Location("dev-simulator").apply {
                latitude = point.latitude
                longitude = point.longitude
                accuracy = 5f
                time = System.currentTimeMillis()
            }
            emit(location)
            index++
            delay(intervalMs)
        }
    }
}
