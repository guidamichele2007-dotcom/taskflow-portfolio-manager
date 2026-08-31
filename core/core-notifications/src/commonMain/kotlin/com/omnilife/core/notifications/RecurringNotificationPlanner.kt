package com.omnilife.core.notifications

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

/**
 * A recurring notification's own cadence — distinct from a domain module's occurrence
 * calculation (e.g. `domain-task`'s `RecurrenceRule`, which this module never depends on: L4
 * core services never depend on L3 domain modules). This models only what the *reminder itself*
 * repeats on: a daily standup nudge, a weekly digest, a custom interval — never a calendar-aware
 * due date.
 */
public sealed interface NotificationRecurrenceRule {
    public data object Daily : NotificationRecurrenceRule

    public data class Weekly(public val daysOfWeek: Set<DayOfWeek>) : NotificationRecurrenceRule {
        init {
            require(daysOfWeek.isNotEmpty()) { "Weekly recurrence requires at least one day of week" }
        }
    }

    public data class CustomInterval(public val days: Int) : NotificationRecurrenceRule {
        init {
            require(days >= 1) { "CustomInterval days must be >= 1, was $days" }
        }
    }
}

/**
 * Computes one occurrence at a time from [previous] — never materializes a whole future series,
 * same "lazy, on demand" principle as `domain-task`'s `RecurrenceCalculator`. No month-end/leap-year
 * clamping is needed here (unlike Monthly/Yearly task recurrence): adding N days is unambiguous.
 */
public object RecurringNotificationPlanner {
    public fun nextOccurrence(
        rule: NotificationRecurrenceRule,
        previous: LocalDateTime,
    ): LocalDateTime =
        when (rule) {
            is NotificationRecurrenceRule.Daily -> addDays(previous, 1)
            is NotificationRecurrenceRule.CustomInterval -> addDays(previous, rule.days)
            is NotificationRecurrenceRule.Weekly -> nextWeeklyOccurrence(rule.daysOfWeek, previous)
        }

    private fun addDays(
        previous: LocalDateTime,
        days: Int,
    ): LocalDateTime {
        val nextDate = previous.date.plus(days, DateTimeUnit.DAY)
        return LocalDateTime(nextDate, previous.time)
    }

    private fun nextWeeklyOccurrence(
        daysOfWeek: Set<DayOfWeek>,
        previous: LocalDateTime,
    ): LocalDateTime {
        for (offset in 1..7) {
            val candidateDate = previous.date.plus(offset, DateTimeUnit.DAY)
            if (candidateDate.dayOfWeek in daysOfWeek) return LocalDateTime(candidateDate, previous.time)
        }
        error("Weekly recurrence has no valid day of week: $daysOfWeek")
    }
}
