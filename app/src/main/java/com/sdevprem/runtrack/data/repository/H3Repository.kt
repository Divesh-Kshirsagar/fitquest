package com.sdevprem.runtrack.data.repository

import com.sdevprem.runtrack.domain.model.ColoredPolygon
import kotlinx.coroutines.flow.StateFlow

interface H3Repository {
    val h3GridState: StateFlow<List<ColoredPolygon>>

    /** Called on every GPS location update from the tracking service. */
    suspend fun updateLocation(lat: Double, lng: Double)

    /** Called every time the step count changes during an active session. */
    suspend fun updateStepCount(steps: Int)

    /**
     * Resets all session state (hex map, polygon list, engine phase).
     * Must be called before each new run starts.
     */
    fun resetSession()
}
