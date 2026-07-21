package com.omnilife.domain.task

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class RecurrenceCalculatorTest {
    @Test
    fun `daily recurrence moves exactly one day forward`() {
        val next = RecurrenceCalculator.nextOccurrence(RecurrenceRule.Daily, LocalDate(2026, 7, 21))
        assertEquals(LocalDate(2026, 7, 22), next)
    }

    @Test
    fun `daily recurrence rolls over into the next month`() {
        val next = RecurrenceCalculator.nextOccurrence(RecurrenceRule.Daily, LocalDate(2026, 7, 31))
        assertEquals(LocalDate(2026, 8, 1), next)
    }

    @Test
    fun `daily recurrence rolls over into the next year`() {
        val next = RecurrenceCalculator.nextOccurrence(RecurrenceRule.Daily, LocalDate(2026, 12, 31))
        assertEquals(LocalDate(2027, 1, 1), next)
    }

    @Test
    fun `custom interval adds the configured number of days`() {
        val next = RecurrenceCalculator.nextOccurrence(RecurrenceRule.CustomInterval(3), LocalDate(2026, 7, 30))
        assertEquals(LocalDate(2026, 8, 2), next)
    }

    @Test
    fun `weekly recurrence finds the next matching day of week`() {
        // 2026-07-21 is a Tuesday; "every Monday" -> next Monday is 2026-07-27.
        val rule = RecurrenceRule.Weekly(setOf(DayOfWeek.MONDAY))
        val next = RecurrenceCalculator.nextOccurrence(rule, LocalDate(2026, 7, 21))
        assertEquals(LocalDate(2026, 7, 27), next)
        assertEquals(DayOfWeek.MONDAY, next.dayOfWeek)
    }

    @Test
    fun `weekly recurrence with multiple days picks the closest one`() {
        // Tuesday 2026-07-21, days = {Mon, Wed, Fri} -> next is Wednesday 2026-07-22.
        val rule = RecurrenceRule.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
        val next = RecurrenceCalculator.nextOccurrence(rule, LocalDate(2026, 7, 21))
        assertEquals(LocalDate(2026, 7, 22), next)
    }

    @Test
    fun `monthly recurrence on a regular day moves to the same day next month`() {
        val rule = RecurrenceRule.Monthly(dayOfMonth = 15)
        val next = RecurrenceCalculator.nextOccurrence(rule, LocalDate(2026, 7, 15))
        assertEquals(LocalDate(2026, 8, 15), next)
    }

    @Test
    fun `monthly recurrence on day 31 clamps to the last day of a shorter month (MFC-E-09)`() {
        // Jan 31 -> next month is February (28 days in 2026, not a leap year).
        val rule = RecurrenceRule.Monthly(dayOfMonth = 31)
        val next = RecurrenceCalculator.nextOccurrence(rule, LocalDate(2026, 1, 31))
        assertEquals(LocalDate(2026, 2, 28), next)
    }

    @Test
    fun `monthly day-31 recurrence rule is not permanently altered by a short month (TASK-R-08 spirit)`() {
        // The rule stays "day 31" even after landing on Feb 28: the following
        // occurrence (March, 31 days) must land back on day 31, not day 28.
        val rule = RecurrenceRule.Monthly(dayOfMonth = 31)
        val afterFebruary = RecurrenceCalculator.nextOccurrence(rule, LocalDate(2026, 2, 28))
        assertEquals(LocalDate(2026, 3, 31), afterFebruary)
    }

    @Test
    fun `monthly recurrence using the last day of month explicitly always lands on the last day`() {
        val rule = RecurrenceRule.Monthly(dayOfMonth = 1, useLastDayOfMonth = true)
        assertEquals(LocalDate(2026, 2, 28), RecurrenceCalculator.nextOccurrence(rule, LocalDate(2026, 1, 15)))
        assertEquals(LocalDate(2026, 4, 30), RecurrenceCalculator.nextOccurrence(rule, LocalDate(2026, 3, 15)))
    }

    @Test
    fun `yearly recurrence on Feb 29 resolves to Feb 28 on a non-leap year (MFC-E-09)`() {
        // 2028 is a leap year -> next year 2029 is not.
        val next = RecurrenceCalculator.nextOccurrence(RecurrenceRule.Yearly, LocalDate(2028, 2, 29))
        assertEquals(LocalDate(2029, 2, 28), next)
    }

    @Test
    fun `yearly recurrence on a regular date moves exactly one year forward`() {
        val next = RecurrenceCalculator.nextOccurrence(RecurrenceRule.Yearly, LocalDate(2026, 7, 21))
        assertEquals(LocalDate(2027, 7, 21), next)
    }

    @Test
    fun `12-month simulation of a weekly recurrence produces exactly one occurrence per week, no duplicates or gaps`() {
        val rule = RecurrenceRule.Weekly(setOf(DayOfWeek.MONDAY))
        var current = LocalDate(2026, 1, 5) // first Monday of 2026
        val occurrences = mutableListOf(current)
        repeat(51) { // ~52 weeks total across the 12 months
            current = RecurrenceCalculator.nextOccurrence(rule, current)
            occurrences.add(current)
        }

        assertEquals(occurrences.size, occurrences.toSet().size, "no duplicate occurrences over 12 months")
        occurrences.forEach { assertEquals(DayOfWeek.MONDAY, it.dayOfWeek) }
        occurrences.zipWithNext().forEach { (a, b) ->
            val gapDays = daysBetween(a, b)
            assertEquals(7, gapDays, "every consecutive occurrence must be exactly 7 days apart")
        }
        // 51 weeks after the first Monday of 2026 (2026 is not a leap year).
        assertEquals(LocalDate(2026, 12, 28), occurrences.last())
    }

    @Test
    fun `12-month simulation of a monthly day-31 recurrence lands on a valid date every month, no Feb 30`() {
        val rule = RecurrenceRule.Monthly(dayOfMonth = 31)
        var current = LocalDate(2026, 1, 31)
        val occurrences = mutableListOf(current)
        repeat(11) {
            current = RecurrenceCalculator.nextOccurrence(rule, current)
            occurrences.add(current)
        }

        assertEquals(12, occurrences.size)
        assertEquals(occurrences.size, occurrences.toSet().size, "no duplicate occurrences over 12 months")
        val expectedDays = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        assertEquals(expectedDays, occurrences.map { it.dayOfMonth })
        val expectedMonths = (1..12).toList()
        assertEquals(expectedMonths, occurrences.map { it.monthNumber })
    }

    private fun daysBetween(a: LocalDate, b: LocalDate): Int {
        var count = 0
        var cursor = a
        while (cursor < b) {
            cursor = RecurrenceCalculator.nextOccurrence(RecurrenceRule.Daily, cursor)
            count++
        }
        return count
    }
}
