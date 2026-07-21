package com.omnilife.domain.task

import kotlinx.datetime.LocalDate

/**
 * Computes the next occurrence date for a [RecurrenceRule]. Pure and
 * deterministic by construction: no I/O, no clock reads — the caller
 * supplies [from] (the date of the occurrence being replaced).
 *
 * Date-limit rules (MFC-E-09) are handled explicitly here rather than
 * delegated to library date arithmetic, to keep the exact clamping
 * behavior (day 31 -> shorter month's last day; Feb 29 -> Feb 28 on a
 * non-leap year) auditable and unit-tested independent of any library's
 * internal month-arithmetic semantics.
 */
public object RecurrenceCalculator {
    public fun nextOccurrence(rule: RecurrenceRule, from: LocalDate): LocalDate = when (rule) {
        is RecurrenceRule.Daily -> addDays(from, 1)
        is RecurrenceRule.CustomInterval -> addDays(from, rule.days)
        is RecurrenceRule.Weekly -> nextWeeklyOccurrence(rule, from)
        is RecurrenceRule.Monthly -> nextMonthlyOccurrence(rule, from)
        is RecurrenceRule.Yearly -> nextYearlyOccurrence(from)
    }

    private fun nextWeeklyOccurrence(rule: RecurrenceRule.Weekly, from: LocalDate): LocalDate {
        for (offset in 1..7) {
            val candidate = addDays(from, offset)
            if (candidate.dayOfWeek in rule.daysOfWeek) return candidate
        }
        error("Weekly recurrence has no valid day of week: ${rule.daysOfWeek}")
    }

    private fun nextMonthlyOccurrence(rule: RecurrenceRule.Monthly, from: LocalDate): LocalDate {
        val (targetYear, targetMonth) = nextMonth(from.year, from.monthNumber)
        val lastDay = daysInMonth(targetYear, targetMonth)
        val day = if (rule.useLastDayOfMonth) lastDay else minOf(rule.dayOfMonth, lastDay)
        return LocalDate(targetYear, targetMonth, day)
    }

    private fun nextYearlyOccurrence(from: LocalDate): LocalDate {
        val targetYear = from.year + 1
        val month = from.monthNumber
        val lastDay = daysInMonth(targetYear, month)
        val day = minOf(from.dayOfMonth, lastDay)
        return LocalDate(targetYear, month, day)
    }

    private fun addDays(date: LocalDate, days: Int): LocalDate {
        var year = date.year
        var month = date.monthNumber
        var day = date.dayOfMonth + days
        while (day > daysInMonth(year, month)) {
            day -= daysInMonth(year, month)
            val (nextYear, nextMonthValue) = nextMonth(year, month)
            year = nextYear
            month = nextMonthValue
        }
        return LocalDate(year, month, day)
    }

    private fun nextMonth(year: Int, month: Int): Pair<Int, Int> =
        if (month == 12) (year + 1) to 1 else year to (month + 1)

    private fun daysInMonth(year: Int, month: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> error("Invalid month $month")
    }

    private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}
