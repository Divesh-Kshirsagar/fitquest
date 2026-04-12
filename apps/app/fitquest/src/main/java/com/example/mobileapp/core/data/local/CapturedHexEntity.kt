package com.example.mobileapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "captured_hexes")
data class CapturedHexEntity(
    @PrimaryKey val hexId: String,
    val totalSteps: Int,
    val lastUpdated: Long
)

