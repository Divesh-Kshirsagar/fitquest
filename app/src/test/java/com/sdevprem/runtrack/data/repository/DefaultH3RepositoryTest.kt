package com.sdevprem.runtrack.data.repository

import com.sdevprem.runtrack.domain.tracking.HexUnlockEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

/**
 * Pure JVM unit tests for [HexUnlockEngine].
 *
 * We inject a simple Euclidean distance function (in "meters" where 1 degree ≈ 111_139 m)
 * so no Android libraries are needed.
 */
private val euclideanDistanceFn: (Double, Double, Double, Double) -> Double =
    { lat1, lng1, lat2, lng2 ->
        val dLat = (lat2 - lat1) * 111_139.0
        val dLng = (lng2 - lng1) * 111_139.0
        sqrt(dLat * dLat + dLng * dLng)
    }

class DefaultH3RepositoryTest {

    // Shorthand: degrees → approximate meters using our test distance fn
    private fun degreesToMeters(degrees: Double) = degrees * 111_139.0

    private fun makeEngine() = HexUnlockEngine(euclideanDistanceFn)

    // ── Phase: READY ────────────────────────────────────────────────────────

    @Test
    fun `first call always unlocks (READY phase)`() {
        val engine = makeEngine()
        assertTrue("First location must unlock immediately", engine.tryUnlock(0.0, 0.0, 0))
    }

    @Test
    fun `after first unlock the engine is in EARLY_UNLOCK_DONE phase`() {
        val engine = makeEngine()
        engine.tryUnlock(37.7749, -122.4194, 0)
        // The last unlock position should now be set
        assertEquals(37.7749, engine.lastUnlockLat!!, 1e-9)
    }

    // ── Phase: EARLY_UNLOCK_DONE – jitter filter ────────────────────────────

    @Test
    fun `micro movement below JITTER_FILTER_METERS does NOT unlock`() {
        val engine = makeEngine()
        engine.tryUnlock(37.7749, -122.4194, 0)             // first → unlocked

        // ~1.1 m (0.00001 deg * 111_139 ≈ 1.1 m) — below jitter threshold (2 m)
        val result = engine.tryUnlock(37.7749 + 0.00001, -122.4194, 0)
        assertFalse("Micro movement must be filtered out", result)
    }

    // ── Phase: EARLY_UNLOCK_DONE – distance trigger ─────────────────────────

    @Test
    fun `movement beyond NORMAL_DISTANCE_METERS unlocks a new hex`() {
        val engine = makeEngine()
        engine.tryUnlock(37.7749, -122.4194, 0)             // first → unlocked

        // 0.0001 deg * 111_139 ≈ 11.1 m — exceeds NORMAL_DISTANCE_METERS (10 m)
        val result = engine.tryUnlock(37.7749 + 0.0001, -122.4194, 0)
        assertTrue("Large enough movement must unlock", result)
    }

    @Test
    fun `movement just below NORMAL_DISTANCE_METERS does NOT unlock by distance`() {
        val engine = makeEngine()
        engine.tryUnlock(0.0, 0.0, 0)                       // first → unlocked

        // ~9 m, below 10 m threshold and above jitter threshold
        val delta = 9.0 / 111_139.0
        val result = engine.tryUnlock(delta, 0.0, 0)
        assertFalse("Sub-threshold movement must not unlock", result)
    }

    // ── Phase: EARLY_UNLOCK_DONE – step trigger ─────────────────────────────

    @Test
    fun `step count delta beyond NORMAL_STEP_DELTA unlocks even without distance`() {
        val engine = makeEngine()
        engine.tryUnlock(0.0, 0.0, 0)                       // first → unlocked

        // Move 5 m (between jitter 2 m and distance 10 m), 20+ steps
        val delta5m = 5.0 / 111_139.0
        val result = engine.tryUnlock(delta5m, 0.0, stepCount = 20)
        assertTrue("20 step delta must trigger unlock", result)
    }

    @Test
    fun `fewer than NORMAL_STEP_DELTA steps and short distance does NOT unlock`() {
        val engine = makeEngine()
        engine.tryUnlock(0.0, 0.0, 0)                       // first → unlocked

        val delta5m = 5.0 / 111_139.0
        val result = engine.tryUnlock(delta5m, 0.0, stepCount = 19)
        assertFalse("19 steps and 5 m must not unlock", result)
    }
}
