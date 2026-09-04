package com.example.mobileapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyQuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<DailyQuestEntity>)

    @Query("SELECT * FROM daily_quests WHERE dateString = :date ORDER BY isCompleted ASC")
    fun observeQuestsForDate(date: String): Flow<List<DailyQuestEntity>>

    @Query("SELECT * FROM daily_quests WHERE dateString = :date")
    suspend fun getQuestsForDate(date: String): List<DailyQuestEntity>

    @Query("UPDATE daily_quests SET currentProgress = :progress, isCompleted = :completed WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, completed: Boolean)
}
