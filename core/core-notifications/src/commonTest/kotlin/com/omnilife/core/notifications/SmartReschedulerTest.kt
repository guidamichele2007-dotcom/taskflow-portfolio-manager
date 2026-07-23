package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class SmartReschedulerTest {
    private fun request(scheduledFor: Instant) =
        NotificationRequest(
            id = "r1",
            category = NotificationCategory("task.reminder", "task"),
            priority = NotificationPriority.PROMEMORIA_UTENTE,
            entityReference = EntityReference("task-1", "task"),
            title = "t",
            body = "b",
            scheduledFor = scheduledFor,
        )

    @Test
    fun `NTF-AC-03 - a reminder for 23h that would wake at 8h is expired (9h later, beyond the relevance window)`() {
        val scheduled = Instant.parse("2026-01-01T23:00:00Z")
        val wake = Instant.parse("2026-01-02T08:00:00Z")

        assertEquals(SmartRescheduleDecision.EXPIRED, SmartRescheduler.decide(request(scheduled), wake))
    }

    @Test
    fun `a reminder still within the relevance window at wake time is shown`() {
        val scheduled = Instant.parse("2026-01-02T05:00:00Z")
        val wake = Instant.parse("2026-01-02T08:00:00Z")

        assertEquals(SmartRescheduleDecision.SHOW_AT_WAKE, SmartRescheduler.decide(request(scheduled), wake))
    }

    @Test
    fun `exactly at the relevance window boundary is not yet expired`() {
        val scheduled = Instant.parse("2026-01-02T04:00:00Z")
        val wake = scheduled + SmartRescheduler.relevanceWindow

        assertEquals(SmartRescheduleDecision.SHOW_AT_WAKE, SmartRescheduler.decide(request(scheduled), wake))
    }
}
