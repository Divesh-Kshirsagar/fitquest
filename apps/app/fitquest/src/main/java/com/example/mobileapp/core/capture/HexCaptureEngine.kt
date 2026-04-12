package com.example.mobileapp.core.capture

import com.example.mobileapp.core.data.local.HexRepository
import com.example.mobileapp.core.geo.HexIndexer
import com.example.mobileapp.core.model.GeoPoint
import com.example.mobileapp.core.sensors.LocationTrackingManager
import com.example.mobileapp.core.sensors.StepSensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HexCaptureSnapshot(
    val isTracking: Boolean = false,
    val currentHexId: String? = null,
    val currentLocation: GeoPoint? = null,
    val sessionSteps: Int = 0,
    val hexesToSteps: Map<String, Int> = emptyMap(),
    val nearbyHexIds: List<String> = emptyList()
)

class HexCaptureEngine(
    private val stepSensorManager: StepSensorManager,
    private val locationTrackingManager: LocationTrackingManager,
    private val hexRepository: HexRepository,
    private val hexIndexer: HexIndexer,
) {
    // TODO(TESTING): Resolution 10 gives ~65 m hex edges — faster capture feedback for dev.
    //  Switch to 9 (~174 m) or higher after field testing for the right gameplay feel.
    private val h3Resolution = 10

    // TODO(TESTING): k=2 gives ~19 visible hexes around the user.
    //  Increase for a wider visible area; decrease for performance on low-end devices.
    private val nearbyRingSize = 2

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(HexCaptureSnapshot())
    val state: StateFlow<HexCaptureSnapshot> = _state.asStateFlow()

    private var locationJob: Job? = null
    private var stepsJob: Job? = null

    fun startTracking() {
        if (_state.value.isTracking) return
        if (!hexIndexer.isAvailable()) return

        _state.update { it.copy(isTracking = true) }

        locationJob = scope.launch {
            locationTrackingManager.observeLocations().collect { location ->
                val hexId = hexIndexer.latLngToHexId(
                    location.latitude,
                    location.longitude,
                    h3Resolution
                )
                val nearby = hexIndexer.getHexesInRadius(
                    location.latitude,
                    location.longitude,
                    h3Resolution,
                    nearbyRingSize
                )
                _state.update {
                    it.copy(
                        currentHexId = hexId,
                        currentLocation = GeoPoint(location.latitude, location.longitude),
                        nearbyHexIds = nearby
                    )
                }
            }
        }

        stepsJob = scope.launch {
            stepSensorManager.observeStepDeltas().collect { delta ->
                _state.update { snapshot ->
                    val targetHex = snapshot.currentHexId ?: return@update snapshot
                    val updated = snapshot.hexesToSteps.toMutableMap()
                    updated[targetHex] = (updated[targetHex] ?: 0) + delta
                    snapshot.copy(
                        sessionSteps = snapshot.sessionSteps + delta,
                        hexesToSteps = updated
                    )
                }
            }
        }
    }

    fun stopTracking() {
        if (!_state.value.isTracking) return

        locationJob?.cancel()
        stepsJob?.cancel()
        locationJob = null
        stepsJob = null

        val finishedSession = _state.value.hexesToSteps
        scope.launch {
            if (finishedSession.isNotEmpty()) {
                hexRepository.mergeSessionHexes(finishedSession)
            }
        }

        _state.update {
            it.copy(
                isTracking = false,
                currentHexId = null,
                currentLocation = null,
                sessionSteps = 0,
                hexesToSteps = emptyMap(),
                nearbyHexIds = emptyList()
            )
        }
    }
}
