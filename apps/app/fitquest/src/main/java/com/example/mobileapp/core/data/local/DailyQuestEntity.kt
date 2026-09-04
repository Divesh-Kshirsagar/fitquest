package com.example.mobileapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetMetric: String, // "STEPS", "HEXES", "DURATION"
    val targetValue: Int,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val rewardXp: Int = 50,
    val dateString: String // YYYY-MM-DD
)
