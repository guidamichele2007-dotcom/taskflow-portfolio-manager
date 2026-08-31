package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.time.Duration.Companion.minutes

class SnoozeManagerTest {
    private val zone = TimeZone.UTC
    private val original =
        NotificationRequest(
            id = "original",
            category = NotificationCategory("task.reminder", "task"),
            priority = NotificationPriority.PROMEMORIA_UTENTE,
            entityReference = EntityReference("task-1", "task"),
            title = "t",
            body = "b",
            scheduledFor = Instant.parse("2026-01-01T15:00:00Z"),
            state = NotificationState.MOSTRATA,
        )

    @Test
    fun `NTF-AC-02 - snoozing produces a new id and PIANIFICATA state`() {
        val now = Instant.parse("2026-01-01T15:01:00Z")
        val snoozed = SnoozeManager.snooze(original, SnoozeOption.FixedDuration(10.minutes), now, zone) { "new-id" }

        assertEquals("new-id", snoozed.id)
        assertNotEquals(original.id, snoozed.id)
        assertEquals(NotificationState.PIANIFICATA, snoozed.state)
    }

    @Test
    fun `FixedDuration advances scheduledFor by exactly the duration from now`() {
        val now = Instant.parse("2026-01-01T15:01:00Z")
        val snoozed = SnoozeManager.snooze(original, SnoozeOption.FixedDuration(10.minutes), now, zone) { "x" }

        assertEquals(now + 10.minutes, snoozed.scheduledFor)
    }

    @Test
    fun `ThisEvening before the evening hour resolves to today`() {
        val now = Instant.parse("2026-01-01T10:00:00Z")
        val snoozed = SnoozeManager.snooze(original, SnoozeOption.ThisEvening(19), now, zone) { "x" }

        assertEquals(Instant.parse("2026-01-01T19:00:00Z"), snoozed.scheduledFor)
    }

    @Test
    fun `ThisEvening after the evening hour rolls to tomorrow`() {
        val now = Instant.parse("2026-01-01T21:00:00Z")
        val snoozed = SnoozeManager.snooze(original, SnoozeOption.ThisEvening(19), now, zone) { "x" }

        assertEquals(Instant.parse("2026-01-02T19:00:00Z"), snoozed.scheduledFor)
    }
}
