package com.example.mobileapp.core.data.local

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface QuestRepository {
    fun observeTodayQuests(): Flow<List<DailyQuestEntity>>
    suspend fun ensureTodayQuests()
    suspend fun recordActivity(steps: Int, hexCount: Int, durationSeconds: Long)
}

class RoomQuestRepository(
    private val questDao: DailyQuestDao,
    private val userProfileRepository: UserProfileRepository
) : QuestRepository {

    private fun todayString(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    override fun observeTodayQuests(): Flow<List<DailyQuestEntity>> {
        return questDao.observeQuestsForDate(todayString())
    }

    override suspend fun ensureTodayQuests() {
        val today = todayString()
        val existing = questDao.getQuestsForDate(today)
        if (existing.isNotEmpty()) return

        val defaultQuests = listOf(
            DailyQuestEntity(
                id = "${today}_steps_3k",
                title = "Trail Blazer",
                description = "Log at least 3,000 steps today",
                targetMetric = "STEPS",
                targetValue = 3000,
                rewardXp = 100,
                dateString = today
            ),
            DailyQuestEntity(
                id = "${today}_hex_2",
                title = "Territory Expansion",
                description = "Capture or reinforce 2 hexagons",
                targetMetric = "HEXES",
                targetValue = 2,
                rewardXp = 150,
                dateString = today
            ),
            DailyQuestEntity(
                id = "${today}_duration_10m",
                title = "Endurance Walk",
                description = "Complete an active capture walk of at least 10 minutes",
                targetMetric = "DURATION",
                targetValue = 600, // 600 seconds = 10 mins
                rewardXp = 120,
                dateString = today
            )
        )
        questDao.insertQuests(defaultQuests)
    }

    override suspend fun recordActivity(steps: Int, hexCount: Int, durationSeconds: Long) {
        val today = todayString()
        val quests = questDao.getQuestsForDate(today)

        for (quest in quests) {
            if (quest.isCompleted) continue

            val delta = when (quest.targetMetric) {
                "STEPS" -> steps
                "HEXES" -> hexCount
                "DURATION" -> durationSeconds.toInt()
                else -> 0
            }

            val newProgress = quest.currentProgress + delta
            val completed = newProgress >= quest.targetValue

            if (completed && !quest.isCompleted) {
                // Award XP to user profile
                val profile = userProfileRepository.getProfile()
                userProfileRepository.saveProfile(profile.copy(
                    xp = profile.xp + quest.rewardXp,
                    level = 1 + ((profile.xp + quest.rewardXp) / 250)
                ))
            }

            questDao.updateProgress(quest.id, newProgress, completed)
        }
    }
}
