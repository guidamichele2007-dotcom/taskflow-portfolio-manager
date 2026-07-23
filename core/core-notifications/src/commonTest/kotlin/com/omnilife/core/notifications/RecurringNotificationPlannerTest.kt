package com.omnilife.core.notifications

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RecurringNotificationPlannerTest {
    @Test
    fun `Daily advances exactly one day, keeping the time`() {
        val previous = LocalDateTime(2026, 1, 1, 9, 0)
        val next = RecurringNotificationPlanner.nextOccurrence(NotificationRecurrenceRule.Daily, previous)
        assertEquals(LocalDateTime(2026, 1, 2, 9, 0), next)
    }

    @Test
    fun `CustomInterval advances by the declared number of days`() {
        val previous = LocalDateTime(2026, 1, 1, 9, 0)
        val next = RecurringNotificationPlanner.nextOccurrence(NotificationRecurrenceRule.CustomInterval(5), previous)
        assertEquals(LocalDateTime(2026, 1, 6, 9, 0), next)
    }

    @Test
    fun `Weekly finds the next declared day of week, skipping undeclared days`() {
        // 2026-01-01 is a Thursday.
        val previous = LocalDateTime(2026, 1, 1, 9, 0)
        val rule = NotificationRecurrenceRule.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))

        val next = RecurringNotificationPlanner.nextOccurrence(rule, previous)

        assertEquals(DayOfWeek.FRIDAY, next.dayOfWeek)
        assertEquals(LocalDateTime(2026, 1, 2, 9, 0), next)
    }

    @Test
    fun `Weekly wraps into the following week when no day matches until then`() {
        // 2026-01-01 Thursday; only Monday declared -> next Monday is 2026-01-05.
        val previous = LocalDateTime(2026, 1, 1, 9, 0)
        val rule = NotificationRecurrenceRule.Weekly(setOf(DayOfWeek.MONDAY))

        val next = RecurringNotificationPlanner.nextOccurrence(rule, previous)

        assertEquals(LocalDateTime(2026, 1, 5, 9, 0), next)
    }

    @Test
    fun `Weekly requires at least one day of week`() {
        assertFailsWith<IllegalArgumentException> { NotificationRecurrenceRule.Weekly(emptySet()) }
    }

    @Test
    fun `CustomInterval requires at least 1 day`() {
        assertFailsWith<IllegalArgumentException> { NotificationRecurrenceRule.CustomInterval(0) }
    }
}
