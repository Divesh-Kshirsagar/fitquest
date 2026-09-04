package com.example.mobileapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String, // "CONQUEST", "ENDURANCE", "STREAK"
    val iconName: String,
    val currentProgress: Int = 0,
    val maxProgress: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)
