package com.example.mobileapp.core.dev

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Emits synthetic step deltas for testing on emulators that lack a hardware step-counter sensor.
 *
 * TODO(TESTING): Adjust [stepsPerEmission] and [intervalMs] to simulate different walking speeds.
 */
class DevStepSimulator {

    /**
     * Emits [stepsPerEmission] steps every [intervalMs] milliseconds.
     * Default: 2 steps per second ≈ a slow walk.
     * Loops forever until the collecting coroutine is cancelled.
     */
    fun simulateSteps(
        stepsPerEmission: Int = 2,
        intervalMs: Long = 1_000L
    ): Flow<Int> = flow {
        while (true) {
            emit(stepsPerEmission)
            delay(intervalMs)
        }
    }
}
