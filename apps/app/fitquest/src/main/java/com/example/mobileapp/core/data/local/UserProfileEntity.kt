package com.example.mobileapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "local_user",
    val username: String = "Scout",
    val avatarName: String = "runner_1",
    val dailyStepGoal: Int = 6000,
    val level: Int = 1,
    val xp: Int = 0,
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val lastActiveDate: String = "", // YYYY-MM-DD
    val totalLifetimeSteps: Int = 0,
    val totalDistanceMeters: Double = 0.0,
    val totalCalories: Int = 0,
    val isOnboardingCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
