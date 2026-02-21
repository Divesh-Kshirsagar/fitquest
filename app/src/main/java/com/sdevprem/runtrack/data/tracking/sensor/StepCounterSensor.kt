package com.sdevprem.runtrack.data.tracking.sensor

import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over Android's step detection sensor.
 * Reports cumulative steps since [start] was last called.
 * Returns 0 safely if the sensor is unavailable on the device.
 */
interface StepCounterSensor {
    /** Cumulative step count since [start]. Always starts at 0. */
    val stepCount: StateFlow<Int>

    /** Begin counting steps from 0. Call on run start. */
    fun start()

    /** Stop counting, unregister sensor. Call on run end. */
    fun stop()
}
