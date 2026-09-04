package com.example.mobileapp.core.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionLogicTest {

    @Test
    fun `level calculation formula scales correctly with xp`() {
        fun calculateLevel(xp: Int): Int = 1 + (xp / 250)

        assertEquals(1, calculateLevel(0))
        assertEquals(1, calculateLevel(100))
        assertEquals(2, calculateLevel(250))
        assertEquals(3, calculateLevel(500))
        assertEquals(5, calculateLevel(1000))
    }

    @Test
    fun `step stride distance and active calories calculation`() {
        val steps = 4000
        val distanceMeters = steps * 0.75
        val caloriesBurned = (steps * 0.04).toInt()

        assertEquals(3000.0, distanceMeters, 0.01)
        assertEquals(160, caloriesBurned)
    }

    @Test
    fun `district tier categorization based on hexagon counts`() {
        fun getTier(hexCount: Int): String = when {
            hexCount >= 50 -> "Hex Master"
            hexCount >= 30 -> "Regional Sovereign"
            hexCount >= 15 -> "Urban Conqueror"
            hexCount >= 5 -> "District Pioneer"
            else -> "Novice Scout"
        }

        assertEquals("Novice Scout", getTier(0))
        assertEquals("Novice Scout", getTier(4))
        assertEquals("District Pioneer", getTier(5))
        assertEquals("Urban Conqueror", getTier(15))
        assertEquals("Regional Sovereign", getTier(35))
        assertEquals("Hex Master", getTier(50))
        assertEquals("Hex Master", getTier(120))
    }
}
