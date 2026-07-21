package com.omnilife.domain.task

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

/**
 * Recurrence rule (TASK-004). Every variant the Functional Bible names:
 * daily, weekly (chosen days), monthly (day-of-month, or the last day of
 * the month), yearly, and a custom "every N days" interval.
 *
 * Occurrence generation is lazy: the next occurrence is computed only when
 * the current one completes or reaches its due date (INV-15) — never all
 * future occurrences materialized in advance. See [RecurrenceCalculator].
 */
@Serializable
public sealed interface RecurrenceRule {
    @Serializable
    public data object Daily : RecurrenceRule

    @Serializable
    public data class Weekly(val daysOfWeek: Set<DayOfWeek>) : RecurrenceRule {
        init {
            require(daysOfWeek.isNotEmpty()) { "Weekly recurrence requires at least one day of week" }
        }
    }

    /**
     * @param dayOfMonth 1..31. If [useLastDayOfMonth] is false and the target
     *   month is shorter than [dayOfMonth], the occurrence falls on that
     *   month's last day instead (MFC-E-09) — the rule itself never changes.
     * @param useLastDayOfMonth always resolves to the month's actual last day.
     */
    @Serializable
    public data class Monthly(val dayOfMonth: Int, val useLastDayOfMonth: Boolean = false) : RecurrenceRule {
        init {
            require(dayOfMonth in 1..31) { "dayOfMonth must be in 1..31, was $dayOfMonth" }
        }
    }

    /**
     * Anchored to the month/day of the occurrence it replaces. Feb 29 on a
     * non-leap target year resolves to Feb 28 (MFC-E-09).
     */
    @Serializable
    public data object Yearly : RecurrenceRule

    @Serializable
    public data class CustomInterval(val days: Int) : RecurrenceRule {
        init {
            require(days >= 1) { "CustomInterval days must be >= 1, was $days" }
        }
    }
}
