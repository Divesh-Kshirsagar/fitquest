package com.sdevprem.runtrack.utils

import com.sdevprem.runtrack.common.extension.setDateToWeekFirstDay
import com.sdevprem.runtrack.common.extension.setDateToWeekLastDay
import com.sdevprem.runtrack.common.extension.setMaximumTime
import com.sdevprem.runtrack.common.extension.setMinimumTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

internal class DateUtilsKtTest {

    lateinit var calendar: Calendar

    @Before
    fun setUp() {
        // Wed 09-Aug-2023 in IST, with an explicit Sunday-first locale (US)
        // so week boundaries are deterministic regardless of the test runner's host locale.
        calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"), java.util.Locale.US).apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DATE, 9)
        }
    }

    @Test
    fun testSetDateToWeekFirstDay_expected_WeekFirstDateWithMinTime() {
        calendar.setDateToWeekFirstDay()
        assertEquals(6, calendar.get(Calendar.DATE))   // Sunday Aug 6
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(0, calendar.get(Calendar.SECOND))
    }

    @Test
    fun testSetDateToWeekLastDay_expected_WeekLastDateWithMaxTime() {
        calendar.setDateToWeekLastDay()
        assertEquals(12, calendar.get(Calendar.DATE))  // Saturday Aug 12
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, calendar.get(Calendar.MINUTE))
        assertEquals(59, calendar.get(Calendar.SECOND))
    }

    @Test
    fun testSetMinimumTime_expected_MinimumTimeOfDay() {
        calendar.setMinimumTime()
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(0, calendar.get(Calendar.SECOND))
    }

    @Test
    fun testSetMaximumTime_expected_MaximumTimeOfDay() {
        calendar.setMaximumTime()
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, calendar.get(Calendar.MINUTE))
        assertEquals(59, calendar.get(Calendar.SECOND))
    }
}