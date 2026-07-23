package com.omnilife.core.notifications

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * MFC-E-07's "orario locale dell'intenzione": a reminder set for 7:00 stays 7:00 *local*
 * regardless of which timezone the device is in when it eventually resolves — never a fixed
 * instant computed once. [wallClock] is intentionally zone-less; [TimezoneHandler.resolve]
 * always takes the *current* zone, so a device that travels re-resolves the same intention to a
 * different instant without the caller doing anything special.
 */
public data class LocalIntention(public val wallClock: LocalDateTime)

/**
 * The single place every other component here converts a wall-clock intention to a concrete
 * [Instant] — so [RecurringNotificationPlanner]/[SnoozeManager]/[QuietHours] never reason about
 * timezones directly.
 *
 * MFC-E-08 (DST): a [wallClock] that falls in a spring-forward gap (e.g. 2:30 on the night clocks
 * jump from 2:00 to 3:00) has no literal instant — `kotlinx-datetime`'s underlying platform
 * conversion resolves it by shifting forward past the gap, which is exactly "l'orario valido più
 * vicino" the Bible requires; this module adds no extra gap-handling on top of that.
 */
public object TimezoneHandler {
    public fun resolve(
        intention: LocalIntention,
        currentZone: TimeZone,
    ): Instant = intention.wallClock.toInstant(currentZone)

    /**
     * The next local `hour:minute` at or after [from] — shared by [QuietHours.nextWindowEnd]
     * (wake time) and `SnoozeManager`'s "this evening" preset, both of which are really "the
     * next occurrence of a specific local clock time," not two different computations.
     */
    public fun nextOccurrenceOfLocalTime(
        from: Instant,
        zone: TimeZone,
        hour: Int,
        minute: Int = 0,
    ): Instant {
        val local = from.toLocalDateTime(zone)
        val todayTarget = LocalDateTime(local.date, LocalTime(hour, minute))
        val todayTargetInstant = todayTarget.toInstant(zone)
        return if (todayTargetInstant > from) {
            todayTargetInstant
        } else {
            LocalDateTime(local.date.plus(1, DateTimeUnit.DAY), LocalTime(hour, minute)).toInstant(zone)
        }
    }
}
