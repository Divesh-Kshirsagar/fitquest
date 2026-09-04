package com.example.mobileapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "run_sessions")
data class RunSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val startedAt: Long,
    val endedAt: Long,
    val durationSeconds: Long,
    val totalSteps: Int,
    val distanceMeters: Double,
    val caloriesBurned: Int,
    val capturedHexCount: Int,
    val capturedHexIdsJson: String, // Comma-separated or JSON string of hex IDs
    val xpEarned: Int
)
