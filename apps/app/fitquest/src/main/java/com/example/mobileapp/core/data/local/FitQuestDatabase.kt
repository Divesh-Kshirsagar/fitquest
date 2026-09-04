package com.example.mobileapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CapturedHexEntity::class,
        UserProfileEntity::class,
        RunSessionEntity::class,
        DailyQuestEntity::class,
        AchievementEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FitQuestDatabase : RoomDatabase() {
    abstract fun hexDao(): HexDao
    abstract fun userDao(): UserDao
    abstract fun runSessionDao(): RunSessionDao
    abstract fun dailyQuestDao(): DailyQuestDao
    abstract fun achievementDao(): AchievementDao
}
