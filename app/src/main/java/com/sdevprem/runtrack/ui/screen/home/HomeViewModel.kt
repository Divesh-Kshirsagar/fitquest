package com.sdevprem.runtrack.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdevprem.runtrack.common.extension.setDateToWeekFirstDay
import com.sdevprem.runtrack.common.extension.setDateToWeekLastDay
import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.data.repository.AppRepository
import com.sdevprem.runtrack.data.repository.UserRepository
import com.sdevprem.runtrack.data.repository.SettingsRepository
import com.sdevprem.runtrack.di.ApplicationScope
import com.sdevprem.runtrack.di.IoDispatcher
import com.sdevprem.runtrack.domain.tracking.TrackingManager
import com.sdevprem.runtrack.domain.usecase.GetCurrentRunStateWithCaloriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import com.sdevprem.runtrack.domain.demo.DemoModeManager
import com.sdevprem.runtrack.data.demo.DemoDataProvider
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository,
    trackingManager: TrackingManager,
    @ApplicationScope
    private val externalScope: CoroutineScope,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    userRepository: UserRepository,
    settingsRepository: SettingsRepository,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase,
    private val demoModeManager: DemoModeManager,
    private val demoDataProvider: DemoDataProvider
) : ViewModel() {


    val durationInMillis = trackingManager.trackingDurationInMs

    val doesUserExist = userRepository.doesUserExist
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    private val calendar = Calendar.getInstance()

    private val distanceCoveredInThisWeekInMeter = repository.getTotalDistance(
        calendar.setDateToWeekFirstDay().time,
        calendar.setDateToWeekLastDay().time
    )

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val homeScreenState = demoModeManager.isDemoMode.flatMapLatest { isDemoMode ->
        combine(
            repository.getRunByDescDateWithLimit(3),
            getCurrentRunStateWithCaloriesUseCase(),
            userRepository.user,
            distanceCoveredInThisWeekInMeter,
            _homeScreenState
        ) { runList, runState, user, distanceInMeter, state ->
            if (isDemoMode) {
                state.copy(
                    runList = demoDataProvider.getDemoRuns().take(3),
                    currentRunStateWithCalories = demoDataProvider.demoRunStateWithCalories,
                    user = user,
                    distanceCoveredInKmInThisWeek = 28.5f,
                    isDemoMode = true
                )
            } else {
                state.copy(
                    runList = runList,
                    currentRunStateWithCalories = runState,
                    user = user,
                    distanceCoveredInKmInThisWeek = distanceInMeter / 1000f,
                    isDemoMode = false
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        HomeScreenState()
    )

    fun toggleDemoMode() {
        demoModeManager.toggle()
    }

    fun deleteRun(run: Run) = externalScope.launch(ioDispatcher) {
        dismissRunDialog()
        repository.deleteRun(run)
    }

    fun showRun(run: Run) {
        _homeScreenState.update { it.copy(currentRunInfo = run) }
    }

    fun dismissRunDialog() {
        _homeScreenState.update { it.copy(currentRunInfo = null) }
    }

}