package com.omnilife.domain.task

import kotlinx.serialization.Serializable

/**
 * Reminder configuration (TASK-003). Only meaningful when the task's due
 * date carries a time-of-day — a date-only due date cannot have a reminder
 * (TASK-002). Scheduling/dispatch of the actual notification is the
 * Notification Broker's job (core-notifications, out of scope this sprint,
 * see Sprint 1 report); this type only models the user's stated intent.
 *
 * @param leadMinutesBeforeDue 0 = remind exactly at the due time.
 */
@Serializable
public data class ReminderConfig(val leadMinutesBeforeDue: Int = 0) {
    init {
        require(leadMinutesBeforeDue >= 0) { "leadMinutesBeforeDue must be >= 0, was $leadMinutesBeforeDue" }
    }
}
