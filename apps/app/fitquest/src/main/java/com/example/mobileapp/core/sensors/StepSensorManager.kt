package com.example.mobileapp.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.max

class StepSensorManager(
    context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun observeStepDeltas(): Flow<Int> = callbackFlow {
        val counterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val detectorSensor = if (counterSensor == null) {
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        } else null

        if (counterSensor != null) {
            var lastAbsoluteValue: Float? = null
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val absolute = event.values.firstOrNull() ?: return
                    val previous = lastAbsoluteValue
                    lastAbsoluteValue = absolute
                    if (previous != null) {
                        val delta = max(0, (absolute - previous).toInt())
                        if (delta > 0) {
                            trySend(delta)
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            sensorManager.registerListener(listener, counterSensor, SensorManager.SENSOR_DELAY_NORMAL)
            awaitClose { sensorManager.unregisterListener(listener) }
        } else if (detectorSensor != null) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val value = event.values.firstOrNull() ?: return
                    if (value > 0f) {
                        trySend(value.toInt().coerceAtLeast(1))
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            sensorManager.registerListener(listener, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL)
            awaitClose { sensorManager.unregisterListener(listener) }
        } else {
            // Hardware step sensor not present; keep flow open without emitting fake steps
            awaitClose { }
        }
    }
}
