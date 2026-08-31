package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** NTF-004: default night window 22-8, evaluated in local time (never a fixed UTC window). */
public data class QuietHoursWindow(public val startHour: Int = 22, public val endHour: Int = 8) {
    init {
        require(startHour in 0..23) { "startHour must be in 0..23, was $startHour" }
        require(endHour in 0..23) { "endHour must be in 0..23, was $endHour" }
    }
}

/**
 * NTF-004: "orari di silenzio" — notte + rispetto dei focus di sistema (focus mode is a platform
 * concern, out of scope here).
 */
public object QuietHours {
    public fun isQuietAt(
        instant: Instant,
        zone: TimeZone,
        window: QuietHoursWindow = QuietHoursWindow(),
    ): Boolean {
        val hour = instant.toLocalDateTime(zone).hour
        return if (window.startHour > window.endHour) {
            // Wraps midnight (the default 22-8): quiet from startHour through 23, then 0 through endHour-1.
            hour >= window.startHour || hour < window.endHour
        } else {
            hour in window.startHour until window.endHour
        }
    }

    /** The next local `endHour:00` strictly after [from] — where a suppressed notification wakes (NTF-004/AC-03). */
    public fun nextWindowEnd(
        from: Instant,
        zone: TimeZone,
        window: QuietHoursWindow = QuietHoursWindow(),
    ): Instant = TimezoneHandler.nextOccurrenceOfLocalTime(from, zone, window.endHour)
}
