package com.example.mobileapp.core.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.example.mobileapp.core.dev.DevLocationSimulator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationServices
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationTrackingManager(
    context: Context,
    private val useDevSimulator: Boolean = false,
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
) {
    private val devSimulator = DevLocationSimulator()

    @SuppressLint("MissingPermission")
    fun observeLocations(
        updateIntervalMs: Long = 2_000L,
        minUpdateIntervalMs: Long = 1_000L
    ): Flow<Location> {
        // TODO(TESTING): When useDevSimulator is true, GPS permission is not needed
        //  and the walk path is defined in DevLocationSimulator.DEFAULT_WALK_PATH.
        if (useDevSimulator) {
            return devSimulator.simulateWalk(intervalMs = updateIntervalMs)
        }

        return callbackFlow {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, updateIntervalMs)
                .setMinUpdateIntervalMillis(minUpdateIntervalMs)
                .build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { trySend(it) }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
            awaitClose { fusedLocationProviderClient.removeLocationUpdates(callback) }
        }
    }
}
