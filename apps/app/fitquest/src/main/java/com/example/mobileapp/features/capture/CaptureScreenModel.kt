package com.example.mobileapp.features.capture

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.mobileapp.core.capture.HexCaptureEngine
import com.example.mobileapp.core.data.local.HexRepository
import com.example.mobileapp.core.geo.HexGeoJsonMapper
import com.example.mobileapp.core.geo.HexIndexer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

class CaptureScreenModel(
    private val hexCaptureEngine: HexCaptureEngine,
    hexRepository: HexRepository,
    private val hexIndexer: HexIndexer
) : ScreenModel, ContainerHost<CaptureState, Nothing> {

    // TODO(refactor-capture-state): migrate captured hex storage to LinkedHashSet in state/event reducers.
    private val screenModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    // TODO(refactor-threshold): move capture threshold to config/remote settings.
    private val captureThresholdSteps = 1

    override val container = screenModelScope.container<CaptureState, Nothing>(CaptureState())

    init {
        // --- Engine snapshot → UI state (runs on Default dispatcher already via engine) ---
        hexCaptureEngine.state
            .onEach { snapshot ->
                intent {
                    val newSessionHexes = snapshot.hexesToSteps
                        .filterValues { it >= captureThresholdSteps }
                        .keys
                        .toList()

                    // Compute GeoJSON strings on the collector's background thread
                    // to avoid H3 JNI calls on the UI thread (prevents "skipped frames").
                    val currentGeoJson = snapshot.currentHexId?.let {
                        HexGeoJsonMapper.toGeoJsonString(hexIndexer, listOf(it))
                    } ?: ""

                    val nearbyGeoJson = if (snapshot.nearbyHexIds.isNotEmpty()) {
                        HexGeoJsonMapper.toGeoJsonString(hexIndexer, snapshot.nearbyHexIds)
                    } else ""

                    reduce {
                        val allCaptured = (newSessionHexes + state.historicalCapturedHexes).distinct()
                        val capturedGeoJson = if (allCaptured.isNotEmpty()) {
                            HexGeoJsonMapper.toGeoJsonString(hexIndexer, allCaptured)
                        } else ""

                        state.copy(
                            isTracking = snapshot.isTracking,
                            currentLocation = snapshot.currentLocation,
                            currentHexId = snapshot.currentHexId,
                            sessionSteps = snapshot.sessionSteps,
                            sessionCapturedHexes = newSessionHexes,
                            allCapturedHexes = allCaptured,
                            capturedHexGeoJson = capturedGeoJson,
                            currentHexGeoJson = currentGeoJson,
                            nearbyHexGeoJson = nearbyGeoJson
                        )
                    }
                }
            }
            .launchIn(screenModelScope)

        // --- Persisted hexes from Room ---
        hexRepository.observeCapturedHexes()
            .onEach { persisted ->
                intent {
                    reduce {
                        val history = persisted.map { it.hexId }
                        val allCaptured = (state.sessionCapturedHexes + history).distinct()
                        val capturedGeoJson = if (allCaptured.isNotEmpty()) {
                            HexGeoJsonMapper.toGeoJsonString(hexIndexer, allCaptured)
                        } else ""

                        state.copy(
                            historicalCapturedHexes = history,
                            allCapturedHexes = allCaptured,
                            capturedHexGeoJson = capturedGeoJson
                        )
                    }
                }
            }
            .launchIn(screenModelScope)
    }

    fun onToggleTracking() = intent {
        if (state.isTracking) {
            hexCaptureEngine.stopTracking()
        } else {
            hexCaptureEngine.startTracking()
        }
    }

    override fun onDispose() {
        hexCaptureEngine.stopTracking()
        screenModelScope.cancel()
    }
}