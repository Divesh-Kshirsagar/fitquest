package com.sdevprem.runtrack.data.tracking.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Uses TYPE_STEP_DETECTOR (fires once per step as a hardware event) rather than
 * TYPE_STEP_COUNTER (cumulative since boot) so we always count from 0 per session
 * without needing to remember the boot-time baseline.
 *
 * Gracefully produces 0 if the sensor is unavailable.
 */
@Singleton
class DefaultStepCounterSensor @Inject constructor(
    @ApplicationContext private val context: Context
) : StepCounterSensor {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _stepCount = MutableStateFlow(0)
    override val stepCount = _stepCount.asStateFlow()

    private var isRegistered = false

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                // Each event = 1 step (values[0] is always 1.0 for STEP_DETECTOR)
                _stepCount.update { it + 1 }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    override fun start() {
        if (isRegistered) return
        _stepCount.value = 0

        if (stepDetector == null) {
            Timber.w("StepDetector sensor not available on this device — step-based unlock disabled")
            return
        }

        val registered = sensorManager.registerListener(
            listener,
            stepDetector,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        isRegistered = registered
        Timber.d("StepCounterSensor started, registered=$registered")
    }

    override fun stop() {
        if (!isRegistered) return
        sensorManager.unregisterListener(listener)
        isRegistered = false
        Timber.d("StepCounterSensor stopped at ${_stepCount.value} steps")
    }
}
