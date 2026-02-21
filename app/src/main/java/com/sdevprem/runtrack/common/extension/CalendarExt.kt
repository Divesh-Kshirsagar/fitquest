package com.sdevprem.runtrack.common.extension

import java.util.Calendar

fun Calendar.setDateToWeekFirstDay() = apply {
    // Compute how many days back from today to reach the first day of the week.
    val currentDow = get(Calendar.DAY_OF_WEEK)   // 1=Sun … 7=Sat
    val firstDow = firstDayOfWeek                 // locale-specific (1=Sun for US/IST)
    val delta = (currentDow - firstDow + 7) % 7  // always 0..6, never negative
    add(Calendar.DAY_OF_YEAR, -delta)
    setMinimumTime()
}

fun Calendar.setDateToWeekLastDay() = apply {
    // Compute how many days forward from today to reach the last day of the week.
    val currentDow = get(Calendar.DAY_OF_WEEK)
    val firstDow = firstDayOfWeek
    val lastDow = (firstDow + 5) % 7 + 1         // first + 6 days, back to 1..7 range
    val delta = (lastDow - currentDow + 7) % 7
    add(Calendar.DAY_OF_YEAR, delta)
    setMaximumTime()
}

fun Calendar.setMinimumTime() = apply {
    set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
    set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
    set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
    set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
}

fun Calendar.setMaximumTime() = apply {
    set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
    set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
    set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
    set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
}

operator fun ClosedRange<Calendar>.iterator() = object : Iterator<Calendar> {
    var current = start

    override fun hasNext(): Boolean {
        (1..2).toList()
        return current <= endInclusive
    }

    override fun next(): Calendar {
        val temp = current
        current = (current.clone() as Calendar).apply {
            add(Calendar.DAY_OF_WEEK, 1)
        }
        return temp
    }
}

fun ClosedRange<Calendar>.toList(): List<Calendar> = buildList {
    for (c in this@toList) add(c)
}