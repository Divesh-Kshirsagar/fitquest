package com.example.mobileapp.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.mobileapp.core.dev.DevStepSimulator
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.max

class StepSensorManager(
    context: Context,
    private val useDevSimulator: Boolean = false
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val devSimulator = DevStepSimulator()

    fun observeStepDeltas(): Flow<Int> {
        // TODO(TESTING): When useDevSimulator is true, hardware step sensor is not needed.
        //  Step rate is configured in DevStepSimulator (default: 2 steps/sec).
        if (useDevSimulator) {
            return devSimulator.simulateSteps()
        }

        return callbackFlow {
            val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepSensor == null) {
                close(IllegalStateException("TYPE_STEP_COUNTER sensor not available"))
                return@callbackFlow
            }

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

            sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
            awaitClose { sensorManager.unregisterListener(listener) }
        }
    }
}
