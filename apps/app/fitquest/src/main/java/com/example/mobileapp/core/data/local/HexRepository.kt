package com.example.mobileapp.core.data.local

import kotlinx.coroutines.flow.Flow

interface HexRepository {
    fun observeCapturedHexes(): Flow<List<CapturedHexEntity>>
    suspend fun mergeSessionHexes(sessionHexesToSteps: Map<String, Int>)
}

class RoomHexRepository(
    private val hexDao: HexDao
) : HexRepository {
    override fun observeCapturedHexes(): Flow<List<CapturedHexEntity>> = hexDao.observeAllCapturedHexes()

    override suspend fun mergeSessionHexes(sessionHexesToSteps: Map<String, Int>) {
        val now = System.currentTimeMillis()
        sessionHexesToSteps.forEach { (hexId, steps) ->
            hexDao.addSteps(hexId = hexId, steps = steps, timestamp = now)
        }
    }
}

