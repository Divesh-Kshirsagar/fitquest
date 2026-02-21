package com.sdevprem.runtrack.domain.tracking



/**
 * Pure domain class that decides when a new H3 hex should be unlocked.
 *
 * Unlock phases:
 *  READY              → initial state; next location call will immediately unlock one hex (session init)
 *  EARLY_UNLOCK_DONE  → one early hex was already unlocked; now normal rules apply
 *
 * Normal rules (after early unlock):
 *  - Unlock if distance from last unlocked hex center ≥ NORMAL_DISTANCE_METERS
 *  - OR step count increased by ≥ NORMAL_STEP_DELTA
 *  - Always ignore movement < JITTER_FILTER_METERS (except the very first point)
 *
 * Early rule (fired at most ONCE, right after session start):
 *  - Unlock if stepDelta ≥ 1  OR  movement ≥ EARLY_DISTANCE_METERS
 *
 * This class is NOT thread-safe by design — callers must confine access to a single
 * coroutine dispatcher (Dispatchers.Default in DefaultH3Repository).
 */
/**
 * Optional injectable distance function — defaults to [androidDistanceBetween].
 * Injecting a custom function makes the engine fully testable on the JVM without Android.
 */
typealias DistanceFn = (lat1: Double, lng1: Double, lat2: Double, lng2: Double) -> Double

/** Production implementation backed by android.location.Location.distanceBetween. */
val androidDistanceBetween: DistanceFn = { lat1, lng1, lat2, lng2 ->
    val r = FloatArray(1)
    android.location.Location.distanceBetween(lat1, lng1, lat2, lng2, r)
    r[0].toDouble()
}

class HexUnlockEngine(
    private val distanceFn: DistanceFn = androidDistanceBetween
) {

    enum class UnlockPhase { READY, EARLY_UNLOCK_DONE }

    private var phase: UnlockPhase = UnlockPhase.READY

    // Position of the last hex that was actually unlocked
    var lastUnlockLat: Double? = null
        private set
    var lastUnlockLng: Double? = null
        private set

    var lastUnlockStepCount: Int = 0
        private set

    companion object {
        const val JITTER_FILTER_METERS = 2.0      // ignore GPS noise
        const val EARLY_DISTANCE_METERS = 2.0     // trigger early unlock
        const val NORMAL_DISTANCE_METERS = 10.0   // trigger normal unlock
        const val NORMAL_STEP_DELTA = 20          // trigger normal unlock by steps
    }

    /**
     * Called with each new GPS location and current session step count.
     * Returns true if the caller should create a new hex at (lat, lng).
     */
    fun tryUnlock(lat: Double, lng: Double, stepCount: Int): Boolean {
        return when (phase) {
            UnlockPhase.READY -> {
                // Session init: always unlock the very first hex, no distance check
                confirmUnlock(lat, lng, stepCount)
                phase = UnlockPhase.EARLY_UNLOCK_DONE
                true
            }

            UnlockPhase.EARLY_UNLOCK_DONE -> {
                val lastLat = lastUnlockLat ?: return false
                val lastLng = lastUnlockLng ?: return false
                val distFromLastUnlock = distanceFn(lastLat, lastLng, lat, lng)
                val stepDelta = stepCount - lastUnlockStepCount

                // Jitter filter: ignore micro-movements
                if (distFromLastUnlock < JITTER_FILTER_METERS) return false

                val shouldUnlock = distFromLastUnlock >= NORMAL_DISTANCE_METERS
                        || stepDelta >= NORMAL_STEP_DELTA

                if (shouldUnlock) {
                    confirmUnlock(lat, lng, stepCount)
                }
                shouldUnlock
            }
        }
    }

    private fun confirmUnlock(lat: Double, lng: Double, stepCount: Int) {
        lastUnlockLat = lat
        lastUnlockLng = lng
        lastUnlockStepCount = stepCount
    }
}
