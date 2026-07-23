package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationBudgetTest {
    private val zone = TimeZone.UTC
    private val day1Morning = Instant.parse("2026-01-01T08:00:00Z")

    @Test
    fun `PROMEMORIA_UTENTE never consumes budget`() {
        val budget = NotificationBudget(initialDailyLimit = 0)

        assertTrue(budget.hasRoom(NotificationPriority.PROMEMORIA_UTENTE, day1Morning, zone))
        budget.consume(NotificationPriority.PROMEMORIA_UTENTE, day1Morning, zone)
        assertEquals(0, budget.shownToday())
    }

    @Test
    fun `NTF-AC-01 - budget 3 allows exactly 3 UTILE notifications then blocks the 4th`() {
        val budget = NotificationBudget(initialDailyLimit = 3)

        repeat(3) {
            assertTrue(budget.hasRoom(NotificationPriority.UTILE, day1Morning, zone))
            budget.consume(NotificationPriority.UTILE, day1Morning, zone)
        }

        assertFalse(budget.hasRoom(NotificationPriority.UTILE, day1Morning, zone))
    }

    @Test
    fun `budget resets at the next local day`() {
        val budget = NotificationBudget(initialDailyLimit = 1)
        budget.consume(NotificationPriority.UTILE, day1Morning, zone)
        assertFalse(budget.hasRoom(NotificationPriority.UTILE, day1Morning, zone))

        val nextDay = Instant.parse("2026-01-02T08:00:00Z")
        assertTrue(budget.hasRoom(NotificationPriority.UTILE, nextDay, zone))
    }

    @Test
    fun `dailyLimit outside 0 to 10 is rejected`() {
        assertFailsWith<IllegalArgumentException> { NotificationBudget(initialDailyLimit = 11) }
        assertFailsWith<IllegalArgumentException> { NotificationBudget(initialDailyLimit = -1) }
    }

    @Test
    fun `dailyLimit can be changed after construction, validated the same way`() {
        val budget = NotificationBudget()
        budget.dailyLimit = 0

        assertFalse(budget.hasRoom(NotificationPriority.UTILE, day1Morning, zone))
        assertFailsWith<IllegalArgumentException> { budget.dailyLimit = 11 }
    }

    @Test
    fun `the day boundary is the local day, not UTC`() {
        val zoneMinus5 = TimeZone.of("America/New_York") // UTC-5 in January, no DST
        val budget = NotificationBudget(initialDailyLimit = 1)

        // 2026-01-01T23:00:00Z is UTC calendar day Jan 1; local (UTC-5) is 2026-01-01T18:00,
        // local day Jan 1.
        budget.consume(NotificationPriority.UTILE, Instant.parse("2026-01-01T23:00:00Z"), zoneMinus5)

        // 2026-01-02T02:00:00Z is UTC calendar day Jan 2 — a *different* UTC day — but local
        // (UTC-5) is 2026-01-01T21:00, still local day Jan 1. A UTC-day-based budget would
        // incorrectly reset here; a local-day-based one must not.
        assertFalse(budget.hasRoom(NotificationPriority.UTILE, Instant.parse("2026-01-02T02:00:00Z"), zoneMinus5))
    }
}
