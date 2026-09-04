package com.example.mobileapp.features.capture

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.mobileapp.core.capture.HexCaptureEngine
import com.example.mobileapp.core.data.local.AchievementRepository
import com.example.mobileapp.core.data.local.HexRepository
import com.example.mobileapp.core.data.local.QuestRepository
import com.example.mobileapp.core.data.local.RunSessionEntity
import com.example.mobileapp.core.data.local.RunSessionRepository
import com.example.mobileapp.core.data.local.UserProfileRepository
import com.example.mobileapp.core.geo.HexGeoJsonMapper
import com.example.mobileapp.core.geo.HexIndexer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import java.util.UUID

class CaptureScreenModel(
    private val hexCaptureEngine: HexCaptureEngine,
    private val hexRepository: HexRepository,
    private val hexIndexer: HexIndexer,
    private val userProfileRepository: UserProfileRepository,
    private val runSessionRepository: RunSessionRepository,
    private val questRepository: QuestRepository,
    private val achievementRepository: AchievementRepository
) : ScreenModel, ContainerHost<CaptureState, Nothing> {

    private val screenModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val captureThresholdSteps = 1
    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0L

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

                    // Compute GeoJSON strings on background to prevent frame drops
                    val currentGeoJson = snapshot.currentHexId?.let {
                        HexGeoJsonMapper.toGeoJsonString(hexIndexer, listOf(it))
                    } ?: ""

                    val nearbyGeoJson = if (snapshot.nearbyHexIds.isNotEmpty()) {
                        HexGeoJsonMapper.toGeoJsonString(hexIndexer, snapshot.nearbyHexIds)
                    } else ""

                    val distanceMeters = snapshot.sessionSteps * 0.75
                    val calories = (snapshot.sessionSteps * 0.04).toInt()

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
                            distanceMeters = distanceMeters,
                            caloriesBurned = calories,
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
            timerJob?.cancel()
            val endTime = System.currentTimeMillis()
            val totalSteps = hexCaptureEngine.state.value.sessionSteps
            val finalStepsMap = hexCaptureEngine.state.value.hexesToSteps
            val capturedHexes = state.sessionCapturedHexes
            val duration = state.durationSeconds
            val distance = state.distanceMeters
            val calories = state.caloriesBurned

            hexCaptureEngine.stopTracking()

            // Calculate XP: +50 per hex, +10 bonus per 100 steps
            val xpEarned = (capturedHexes.size * 50) + ((totalSteps / 100) * 10) + 20

            val session = RunSessionEntity(
                id = UUID.randomUUID().toString(),
                startedAt = sessionStartTime,
                endedAt = endTime,
                durationSeconds = duration,
                totalSteps = totalSteps,
                distanceMeters = distance,
                caloriesBurned = calories,
                capturedHexCount = capturedHexes.size,
                capturedHexIdsJson = capturedHexes.joinToString(","),
                xpEarned = xpEarned
            )

            screenModelScope.launch(Dispatchers.IO) {
                // Save session locally
                runSessionRepository.saveSession(session)

                // Update user profile stats & streak
                val updatedProfile = userProfileRepository.recordCompletedSession(
                    steps = totalSteps,
                    distanceMeters = distance,
                    calories = calories,
                    hexCount = capturedHexes.size,
                    xp = xpEarned
                )

                // Update daily quests
                questRepository.recordActivity(
                    steps = totalSteps,
                    hexCount = capturedHexes.size,
                    durationSeconds = duration
                )

                // Evaluate achievements
                val allHexCount = hexRepository.observeCapturedHexes().first().size
                val totalSessions = runSessionRepository.observeSessionCount().first()
                val unlocked = achievementRepository.evaluateAchievements(
                    totalHexes = allHexCount,
                    lifetimeSteps = updatedProfile.totalLifetimeSteps,
                    totalSessions = totalSessions,
                    currentStreak = updatedProfile.currentStreak
                )

                intent {
                    reduce {
                        state.copy(
                            isTracking = false,
                            isPaused = false,
                            showSummaryDialog = true,
                            latestCompletedSession = session,
                            unlockedAchievements = unlocked
                        )
                    }
                }
            }
        } else {
            sessionStartTime = System.currentTimeMillis()
            reduce {
                state.copy(
                    isTracking = true,
                    isPaused = false,
                    durationSeconds = 0L,
                    distanceMeters = 0.0,
                    caloriesBurned = 0
                )
            }
            hexCaptureEngine.startTracking()
            startTimer()
        }
    }

    fun onTogglePause() = intent {
        val newPaused = !state.isPaused
        reduce { state.copy(isPaused = newPaused) }
    }

    fun dismissSummaryDialog() = intent {
        reduce {
            state.copy(
                showSummaryDialog = false,
                latestCompletedSession = null,
                unlockedAchievements = emptyList()
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = screenModelScope.launch {
            while (isActive) {
                delay(1000)
                intent {
                    if (state.isTracking && !state.isPaused) {
                        reduce { state.copy(durationSeconds = state.durationSeconds + 1) }
                    }
                }
            }
        }
    }

    override fun onDispose() {
        timerJob?.cancel()
        hexCaptureEngine.stopTracking()
        screenModelScope.cancel()
    }
}