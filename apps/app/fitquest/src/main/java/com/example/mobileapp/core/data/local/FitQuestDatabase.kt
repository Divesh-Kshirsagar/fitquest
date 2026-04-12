package com.example.mobileapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CapturedHexEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FitQuestDatabase : RoomDatabase() {
    abstract fun hexDao(): HexDao
}

