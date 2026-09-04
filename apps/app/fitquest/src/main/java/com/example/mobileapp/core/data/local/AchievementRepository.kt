package com.example.mobileapp.core.data.local

import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAchievements(): Flow<List<AchievementEntity>>
    suspend fun ensureDefaultAchievements()
    suspend fun evaluateAchievements(
        totalHexes: Int,
        lifetimeSteps: Int,
        totalSessions: Int,
        currentStreak: Int
    ): List<AchievementEntity>
}

class RoomAchievementRepository(
    private val achievementDao: AchievementDao,
    private val userProfileRepository: UserProfileRepository
) : AchievementRepository {

    override fun observeAchievements(): Flow<List<AchievementEntity>> {
        return achievementDao.observeAllAchievements()
    }

    override suspend fun ensureDefaultAchievements() {
        val defaults = listOf(
            AchievementEntity(
                id = "first_hex",
                title = "First Footprint",
                description = "Capture your very first territory hexagon",
                category = "CONQUEST",
                iconName = "flag",
                maxProgress = 1
            ),
            AchievementEntity(
                id = "hex_collector_10",
                title = "Hex Explorer",
                description = "Capture 10 unique hexagons",
                category = "CONQUEST",
                iconName = "map",
                maxProgress = 10
            ),
            AchievementEntity(
                id = "hex_lord_50",
                title = "Hex Lord",
                description = "Conquer 50 hexagons across your city",
                category = "CONQUEST",
                iconName = "crown",
                maxProgress = 50
            ),
            AchievementEntity(
                id = "steps_10k",
                title = "10k Champion",
                description = "Accumulate 10,000 lifetime steps in FitQuest",
                category = "ENDURANCE",
                iconName = "directions_run",
                maxProgress = 10000
            ),
            AchievementEntity(
                id = "steps_50k",
                title = "Marathoner",
                description = "Accumulate 50,000 lifetime steps in FitQuest",
                category = "ENDURANCE",
                iconName = "stars",
                maxProgress = 50000
            ),
            AchievementEntity(
                id = "streak_3",
                title = "Habit Builder",
                description = "Maintain a 3-day active streak",
                category = "STREAK",
                iconName = "local_fire_department",
                maxProgress = 3
            ),
            AchievementEntity(
                id = "streak_7",
                title = "Unstoppable",
                description = "Maintain a 7-day active streak",
                category = "STREAK",
                iconName = "whatshot",
                maxProgress = 7
            ),
            AchievementEntity(
                id = "sessions_5",
                title = "Consistent Ranger",
                description = "Complete 5 recorded capture sessions",
                category = "ENDURANCE",
                iconName = "fitness_center",
                maxProgress = 5
            )
        )
        achievementDao.insertDefaultAchievements(defaults)
    }

    override suspend fun evaluateAchievements(
        totalHexes: Int,
        lifetimeSteps: Int,
        totalSessions: Int,
        currentStreak: Int
    ): List<AchievementEntity> {
        val unlockedList = mutableListOf<AchievementEntity>()

        fun check(id: String, currentVal: Int) {
            // Synchronously evaluate
            kotlinx.coroutines.runBlocking {
                val ach = achievementDao.getById(id) ?: return@runBlocking
                if (ach.isUnlocked) return@runBlocking

                val newProgress = minOf(currentVal, ach.maxProgress)
                val unlocked = newProgress >= ach.maxProgress
                val timestamp = if (unlocked) System.currentTimeMillis() else null

                achievementDao.updateAchievementProgress(id, newProgress, unlocked, timestamp)
                if (unlocked) {
                    unlockedList.add(ach.copy(isUnlocked = true, unlockedAt = timestamp))
                }
            }
        }

        check("first_hex", totalHexes)
        check("hex_collector_10", totalHexes)
        check("hex_lord_50", totalHexes)
        check("steps_10k", lifetimeSteps)
        check("steps_50k", lifetimeSteps)
        check("streak_3", currentStreak)
        check("streak_7", currentStreak)
        check("sessions_5", totalSessions)

        if (unlockedList.isNotEmpty()) {
            val profile = userProfileRepository.getProfile()
            val bonusXp = unlockedList.size * 200
            userProfileRepository.saveProfile(profile.copy(
                xp = profile.xp + bonusXp,
                level = 1 + ((profile.xp + bonusXp) / 250)
            ))
        }

        return unlockedList
    }
}
