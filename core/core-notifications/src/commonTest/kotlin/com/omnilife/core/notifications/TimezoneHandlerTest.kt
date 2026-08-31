package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TimezoneHandlerTest {
    @Test
    fun `resolve converts a wall-clock intention using the given zone`() {
        val intention = LocalIntention(LocalDateTime(2026, 1, 1, 7, 0))
        val resolved = TimezoneHandler.resolve(intention, TimeZone.UTC)
        assertEquals(LocalDateTime(2026, 1, 1, 7, 0), resolved.toLocalDateTime(TimeZone.UTC))
    }

    @Test
    fun `MFC-E-07 - the same intention resolves to a different instant in a different zone`() {
        val intention = LocalIntention(LocalDateTime(2026, 1, 1, 7, 0))
        val resolvedUtc = TimezoneHandler.resolve(intention, TimeZone.UTC)
        val resolvedTokyo = TimezoneHandler.resolve(intention, TimeZone.of("Asia/Tokyo"))
        // A "7am local" reminder is a different absolute instant depending on where "local" is —
        // the whole point of MFC-E-07 is that the wall-clock 7:00 never changes, the instant does.
        assertFalse(resolvedUtc == resolvedTokyo)
        assertEquals(LocalDateTime(2026, 1, 1, 7, 0), resolvedTokyo.toLocalDateTime(TimeZone.of("Asia/Tokyo")))
    }

    @Test
    fun `nextOccurrenceOfLocalTime from before the target hour returns today`() {
        val from = Instant.parse("2026-01-01T06:00:00Z")
        val next = TimezoneHandler.nextOccurrenceOfLocalTime(from, TimeZone.UTC, hour = 19)
        assertEquals(Instant.parse("2026-01-01T19:00:00Z"), next)
    }

    @Test
    fun `nextOccurrenceOfLocalTime from after the target hour returns tomorrow`() {
        val from = Instant.parse("2026-01-01T20:00:00Z")
        val next = TimezoneHandler.nextOccurrenceOfLocalTime(from, TimeZone.UTC, hour = 19)
        assertEquals(Instant.parse("2026-01-02T19:00:00Z"), next)
    }
}
