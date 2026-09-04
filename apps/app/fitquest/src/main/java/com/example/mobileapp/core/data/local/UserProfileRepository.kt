package com.example.mobileapp.core.data.local

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface UserProfileRepository {
    fun observeProfile(): Flow<UserProfileEntity?>
    suspend fun getProfile(): UserProfileEntity
    suspend fun saveProfile(profile: UserProfileEntity)
    suspend fun completeOnboarding()
    suspend fun updateDailyGoal(goal: Int)
    suspend fun recordCompletedSession(
        steps: Int,
        distanceMeters: Double,
        calories: Int,
        hexCount: Int,
        xp: Int
    ): UserProfileEntity
}

class RoomUserProfileRepository(
    private val userDao: UserDao
) : UserProfileRepository {

    override fun observeProfile(): Flow<UserProfileEntity?> = userDao.observeProfile()

    override suspend fun getProfile(): UserProfileEntity {
        val existing = userDao.getProfileById()
        if (existing != null) return existing
        val defaultProfile = UserProfileEntity(
            lastActiveDate = todayString()
        )
        userDao.upsertProfile(defaultProfile)
        return defaultProfile
    }

    override suspend fun saveProfile(profile: UserProfileEntity) {
        userDao.upsertProfile(profile)
    }

    override suspend fun completeOnboarding() {
        val current = getProfile()
        userDao.upsertProfile(current.copy(isOnboardingCompleted = true))
    }

    override suspend fun updateDailyGoal(goal: Int) {
        userDao.updateDailyGoal(goal)
    }

    override suspend fun recordCompletedSession(
        steps: Int,
        distanceMeters: Double,
        calories: Int,
        hexCount: Int,
        xp: Int
    ): UserProfileEntity {
        val current = getProfile()
        val today = todayString()

        // Streak calculation
        val isConsecutive = isConsecutiveDay(current.lastActiveDate, today)
        val isSameDay = current.lastActiveDate == today
        val newStreak = when {
            isSameDay -> current.currentStreak
            isConsecutive -> current.currentStreak + 1
            else -> 1
        }
        val longestStreak = maxOf(newStreak, current.longestStreak)

        val newLifetimeSteps = current.totalLifetimeSteps + steps
        val newLifetimeDistance = current.totalDistanceMeters + distanceMeters
        val newLifetimeCalories = current.totalCalories + calories
        val newTotalXp = current.xp + xp

        // Simple level progression formula: Level = 1 + (XP / 250)
        val newLevel = 1 + (newTotalXp / 250)

        val updated = current.copy(
            totalLifetimeSteps = newLifetimeSteps,
            totalDistanceMeters = newLifetimeDistance,
            totalCalories = newLifetimeCalories,
            xp = newTotalXp,
            level = newLevel,
            currentStreak = newStreak,
            longestStreak = longestStreak,
            lastActiveDate = today
        )
        userDao.upsertProfile(updated)
        return updated
    }

    private fun todayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun isConsecutiveDay(prevDateStr: String, currentDateStr: String): Boolean {
        if (prevDateStr.isEmpty()) return false
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val prev = format.parse(prevDateStr)?.time ?: return false
            val curr = format.parse(currentDateStr)?.time ?: return false
            val diffHours = (curr - prev) / (1000 * 60 * 60)
            diffHours in 20..48
        } catch (_: Exception) {
            false
        }
    }
}
