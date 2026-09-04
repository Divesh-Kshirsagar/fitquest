package com.example.mobileapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String = "local_user"): UserProfileEntity?

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun observeProfile(id: String = "local_user"): Flow<UserProfileEntity?>

    @Query("UPDATE user_profile SET isOnboardingCompleted = 1 WHERE id = :id")
    suspend fun setOnboardingCompleted(id: String = "local_user")

    @Query("UPDATE user_profile SET dailyStepGoal = :goal WHERE id = :id")
    suspend fun updateDailyGoal(goal: Int, id: String = "local_user")
}
