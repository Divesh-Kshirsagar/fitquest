package com.sdevprem.runtrack.data.repository

import com.google.android.gms.maps.model.LatLng
import com.sdevprem.runtrack.domain.model.ColoredPolygon
import com.sdevprem.runtrack.domain.tracking.HexUnlockEngine
import com.uber.h3core.H3Core
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data/cache layer for H3 hex polygons.
 *
 * All state mutations are guarded by [mutex] and run on [Dispatchers.Default],
 * keeping the main thread free and preventing ConcurrentModificationException.
 *
 * Unlock decisions are fully delegated to [HexUnlockEngine] — this class only
 * resolves H3 indices, manages the boundary cache, and emits immutable snapshots.
 */
@Singleton
class DefaultH3Repository @Inject constructor() : H3Repository {

    // ── H3 Core ─────────────────────────────────────────────────────────────
    private val h3Core: H3Core? = try {
        H3Core.newInstance()
    } catch (t: Throwable) {
        Timber.e(t, "Failed to initialize H3Core — falling back to mock mode")
        null
    }
    private val isMockMode = h3Core == null

    // ── Concurrency ──────────────────────────────────────────────────────────
    /** Mutex ensures single-writer semantics across location + step updates. */
    private val mutex = Mutex()

    // ── Session State (reset on each new run) ────────────────────────────────
    @Volatile private var engine = HexUnlockEngine()
    @Volatile private var currentStepCount = 0

    /** Immutable polygon map — never mutated in-place; always replaced with a new copy. */
    private var polygonSnapshot: Map<Long, ColoredPolygon> = emptyMap()

    /** Cache for hex boundary vertices to avoid re-computation. Persists across sessions. */
    private val boundaryCache = LinkedHashMap<Long, List<LatLng>>(MAX_HEX_COUNT, 0.75f, true)

    // ── Public State ─────────────────────────────────────────────────────────
    private val _h3GridState = MutableStateFlow<List<ColoredPolygon>>(emptyList())
    override val h3GridState = _h3GridState.asStateFlow()

    // ── Public API ───────────────────────────────────────────────────────────

    override fun resetSession() {
        // Safe to call from any thread — replacements are atomic references
        engine = HexUnlockEngine()
        currentStepCount = 0
        polygonSnapshot = emptyMap()
        _h3GridState.value = emptyList()
        Timber.d("H3Repository: session reset")
    }

    override suspend fun updateStepCount(steps: Int) = withContext(Dispatchers.Default) {
        currentStepCount = steps
        // Steps alone don't emit — they are consumed by the engine on the next location update
    }

    override suspend fun updateLocation(lat: Double, lng: Double) = withContext(Dispatchers.Default) {
        mutex.withLock {
            if (polygonSnapshot.size >= MAX_HEX_COUNT) {
                Timber.w("H3Repository: MAX_HEX_COUNT ($MAX_HEX_COUNT) reached — skipping")
                return@withLock
            }

            val shouldUnlock = engine.tryUnlock(lat, lng, currentStepCount)
            if (!shouldUnlock) return@withLock

            if (isMockMode) {
                processMockHex(lat, lng)
            } else {
                processRealHex(lat, lng)
            }
        }
    }

    // ── Real H3 Mode ─────────────────────────────────────────────────────────

    private fun processRealHex(lat: Double, lng: Double) {
        val core = h3Core ?: return
        val h3Index = core.latLngToCell(lat, lng, DEFAULT_RES)

        val existingPolygon = polygonSnapshot[h3Index]
        val newVisitCount = (existingPolygon?.let { visitCountFromColor(it.color) } ?: 0) + 1
        val newColor = colorForVisitCount(newVisitCount)

        if (existingPolygon != null && existingPolygon.color == newColor) return

        val boundary = boundaryCache.getOrPut(h3Index) {
            core.cellToBoundary(h3Index).map { LatLng(it.lat, it.lng) }
        }

        val newPolygon = ColoredPolygon(id = h3Index, points = boundary, color = newColor)
        // Always create a new map copy — never mutate in-place
        polygonSnapshot = polygonSnapshot + (h3Index to newPolygon)
        emitState()
    }

    // ── Mock Mode ────────────────────────────────────────────────────────────

    private fun processMockHex(lat: Double, lng: Double) {
        val latStep = MOCK_GRID_STEP
        val lngStep = MOCK_GRID_STEP
        val gridLat = Math.round(lat / latStep) * latStep
        val gridLng = Math.round(lng / lngStep) * lngStep
        val mockId = (gridLat * 1_000_000 + gridLng * 1_000_000).toLong()

        val existingPolygon = polygonSnapshot[mockId]
        val newVisitCount = (existingPolygon?.let { visitCountFromColor(it.color) } ?: 0) + 1
        val newColor = colorForVisitCount(newVisitCount)

        if (existingPolygon != null && existingPolygon.color == newColor) return

        val boundary = boundaryCache.getOrPut(mockId) {
            createMockHexagon(LatLng(gridLat, gridLng))
        }

        val newPolygon = ColoredPolygon(id = mockId, points = boundary, color = newColor)
        polygonSnapshot = polygonSnapshot + (mockId to newPolygon)
        emitState()
    }

    private fun createMockHexagon(center: LatLng): List<LatLng> {
        return (0 until 6).map { i ->
            val angle = 2.0 * Math.PI / 6.0 * i
            LatLng(
                center.latitude + MOCK_HEX_RADIUS * Math.cos(angle),
                center.longitude + MOCK_HEX_RADIUS * Math.sin(angle)
            )
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun emitState() {
        // Emit an immutable ArrayList snapshot — safe for UI to iterate without synchronization
        _h3GridState.value = ArrayList(polygonSnapshot.values)
    }

    private fun colorForVisitCount(count: Int): Long = when {
        count >= 7 -> 0x80F44336L // Red
        count >= 4 -> 0x80FFEB3BL // Yellow
        count >= 2 -> 0x804CAF50L // Green
        else       -> 0x802196F3L // Blue (first visit)
    }

    /** Reverse-maps a color back to an approximate visit count for increment logic. */
    private fun visitCountFromColor(color: Long): Int = when (color) {
        0x80F44336L -> 7
        0x80FFEB3BL -> 4
        0x804CAF50L -> 2
        else        -> 1
    }

    companion object {
        const val DEFAULT_RES = 12              // ~44m edge length
        private const val MOCK_HEX_RADIUS = 0.00015
        private const val MOCK_GRID_STEP = 0.0002
        private const val MAX_HEX_COUNT = 3000
    }
}
