package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuietHoursTest {
    private val zone = TimeZone.UTC

    @Test
    fun `default window - 23h is quiet`() {
        assertTrue(QuietHours.isQuietAt(Instant.parse("2026-01-01T23:00:00Z"), zone))
    }

    @Test
    fun `default window - 3h is quiet (wraps past midnight)`() {
        assertTrue(QuietHours.isQuietAt(Instant.parse("2026-01-01T03:00:00Z"), zone))
    }

    @Test
    fun `default window - 8h is not quiet (window end is exclusive)`() {
        assertFalse(QuietHours.isQuietAt(Instant.parse("2026-01-01T08:00:00Z"), zone))
    }

    @Test
    fun `default window - noon is not quiet`() {
        assertFalse(QuietHours.isQuietAt(Instant.parse("2026-01-01T12:00:00Z"), zone))
    }

    @Test
    fun `a non-wrapping window, for example 13-15, works the same way`() {
        val window = QuietHoursWindow(startHour = 13, endHour = 15)
        assertTrue(QuietHours.isQuietAt(Instant.parse("2026-01-01T14:00:00Z"), zone, window))
        assertFalse(QuietHours.isQuietAt(Instant.parse("2026-01-01T16:00:00Z"), zone, window))
    }

    @Test
    fun `nextWindowEnd from within the quiet window is today's wake hour`() {
        val end = QuietHours.nextWindowEnd(Instant.parse("2026-01-01T03:00:00Z"), zone)
        assertEquals(Instant.parse("2026-01-01T08:00:00Z"), end)
    }

    @Test
    fun `nextWindowEnd from before the quiet window starts rolls to tomorrow's wake hour`() {
        val end = QuietHours.nextWindowEnd(Instant.parse("2026-01-01T12:00:00Z"), zone)
        assertEquals(Instant.parse("2026-01-02T08:00:00Z"), end)
    }
}
