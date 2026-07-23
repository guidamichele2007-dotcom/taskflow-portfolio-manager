package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * NTF-002: default 3/day (configurable 0-10). [NotificationPriority.PROMEMORIA_UTENTE] never
 * consumes budget — "ciò che l'utente ha chiesto è suo; ciò che proponiamo noi è contingentato."
 * The "day" boundary is the device's *current* local day (MFC-E-07: "la giornata... è quella del
 * fuso corrente del device"), so budget resets at local midnight, never UTC midnight.
 */
public class NotificationBudget(initialDailyLimit: Int = DEFAULT_DAILY_LIMIT) {
    public var dailyLimit: Int = initialDailyLimit
        set(value) {
            require(value in 0..MAX_DAILY_LIMIT) { "dailyLimit must be in 0..$MAX_DAILY_LIMIT, was $value" }
            field = value
        }

    private var currentLocalDay: LocalDate? = null
    private var shownToday = 0

    init {
        require(initialDailyLimit in 0..MAX_DAILY_LIMIT) {
            "dailyLimit must be in 0..$MAX_DAILY_LIMIT, was $initialDailyLimit"
        }
    }

    /** True if one more UTILE/INFORMATIVA notification right now would still be within budget. */
    public fun hasRoom(
        priority: NotificationPriority,
        now: Instant,
        zone: TimeZone,
    ): Boolean {
        if (priority == NotificationPriority.PROMEMORIA_UTENTE) return true
        rolloverIfNewDay(now, zone)
        return shownToday < dailyLimit
    }

    /** Call once a UTILE/INFORMATIVA notification is actually shown; a no-op for PROMEMORIA_UTENTE. */
    public fun consume(
        priority: NotificationPriority,
        now: Instant,
        zone: TimeZone,
    ) {
        if (priority == NotificationPriority.PROMEMORIA_UTENTE) return
        rolloverIfNewDay(now, zone)
        shownToday++
    }

    public fun shownToday(): Int = shownToday

    private fun rolloverIfNewDay(
        now: Instant,
        zone: TimeZone,
    ) {
        val today = now.toLocalDateTime(zone).date
        if (currentLocalDay != today) {
            currentLocalDay = today
            shownToday = 0
        }
    }

    public companion object {
        public const val DEFAULT_DAILY_LIMIT: Int = 3
        public const val MAX_DAILY_LIMIT: Int = 10
    }
}
