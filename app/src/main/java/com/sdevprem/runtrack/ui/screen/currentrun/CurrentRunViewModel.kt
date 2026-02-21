package com.sdevprem.runtrack.ui.screen.currentrun

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.data.demo.DemoDataProvider
import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.data.repository.AppRepository
import com.sdevprem.runtrack.data.repository.H3Repository
import com.sdevprem.runtrack.data.tracking.sensor.StepCounterSensor
import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.IoDispatcher
import com.sdevprem.runtrack.domain.demo.DemoModeManager
import com.sdevprem.runtrack.domain.model.CurrentRunStateWithCalories
import com.sdevprem.runtrack.domain.tracking.TrackingManager
import com.sdevprem.runtrack.domain.tracking.model.lastLocationPoint
import com.sdevprem.runtrack.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CurrentRunViewModel @Inject constructor(
    private val trackingManager: TrackingManager,
    private val repository: AppRepository,
    @ApplicationScope private val appCoroutineScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase,
    private val h3Repository: H3Repository,
    private val stepCounterSensor: StepCounterSensor,
    private val demoModeManager: DemoModeManager,
    private val demoDataProvider: DemoDataProvider
) : ViewModel() {

    // ── Run State ─────────────────────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentRunStateWithCalories = demoModeManager.isDemoMode.flatMapLatest { isDemo ->
        if (isDemo) flowOf(demoDataProvider.demoRunStateWithCalories)
        else getCurrentRunStateWithCaloriesUseCase()
    }.stateIn(viewModelScope, SharingStarted.Lazily, CurrentRunStateWithCalories())

    @OptIn(ExperimentalCoroutinesApi::class)
    val runningDurationInMillis = demoModeManager.isDemoMode.flatMapLatest { isDemo ->
        if (isDemo) flowOf(3_900_000L)
        else trackingManager.trackingDurationInMs
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val h3Polygons = demoModeManager.isDemoMode.flatMapLatest { isDemo ->
        if (isDemo) flowOf(demoDataProvider.demoH3Polygons)
        else h3Repository.h3GridState
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isDemoMode = demoModeManager.isDemoMode
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // ── Initialisation ────────────────────────────────────────────────────────

    init {
        // Forward GPS location to H3 repository (real mode only)
        currentRunStateWithCalories
            .onEach { state ->
                if (demoModeManager.isDemoMode.value) return@onEach
                state.currentRunState.pathPoints.lastLocationPoint()?.let { point ->
                    Timber.d("Location → H3: ${point.locationInfo.latitude}, ${point.locationInfo.longitude}")
                    viewModelScope.launch(ioDispatcher) {
                        h3Repository.updateLocation(
                            point.locationInfo.latitude,
                            point.locationInfo.longitude
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        // Forward step count to H3 repository (real mode only)
        stepCounterSensor.stepCount
            .onEach { steps ->
                if (demoModeManager.isDemoMode.value) return@onEach
                viewModelScope.launch(ioDispatcher) {
                    h3Repository.updateStepCount(steps)
                }
            }
            .launchIn(viewModelScope)

        // Pause real tracking when demo mode is enabled
        demoModeManager.isDemoMode
            .onEach { isDemo -> if (isDemo) trackingManager.pauseTracking() }
            .launchIn(viewModelScope)
    }

    // ── Public Actions ────────────────────────────────────────────────────────

    fun playPauseTracking() {
        if (demoModeManager.isDemoMode.value) return
        if (currentRunStateWithCalories.value.currentRunState.isTracking) {
            trackingManager.pauseTracking()
        } else {
            // Reset session on first resume (i.e., run start)
            if (trackingManager.trackingDurationInMs.value == 0L) {
                h3Repository.resetSession()
                stepCounterSensor.start()
                Timber.d("New run session started — H3 and step sensor reset")
            }
            trackingManager.startResumeTracking()
        }
    }

    fun finishRun(bitmap: Bitmap) {
        if (demoModeManager.isDemoMode.value) return

        trackingManager.pauseTracking()
        stepCounterSensor.stop()

        saveRun(
            Run(
                img = bitmap,
                avgSpeedInKMH = currentRunStateWithCalories.value.currentRunState.distanceInMeters
                    .toBigDecimal()
                    .multiply(3600.toBigDecimal())
                    .divide(
                        if (runningDurationInMillis.value > 0)
                            runningDurationInMillis.value.toBigDecimal()
                        else java.math.BigDecimal.ONE,
                        2,
                        RoundingMode.HALF_UP
                    )
                    .toFloat(),
                distanceInMeters = currentRunStateWithCalories.value.currentRunState.distanceInMeters,
                durationInMillis = runningDurationInMillis.value,
                timestamp = Date(),
                caloriesBurned = currentRunStateWithCalories.value.caloriesBurnt
            )
        )
        trackingManager.stop()
    }

    private fun saveRun(run: Run) = appCoroutineScope.launch(ioDispatcher) {
        repository.insertRun(run)
    }
}