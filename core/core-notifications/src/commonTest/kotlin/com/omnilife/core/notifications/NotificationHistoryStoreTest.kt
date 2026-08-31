package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationHistoryStoreTest {
    private fun request(
        id: String,
        categoryId: String = "task.reminder",
    ) = NotificationRequest(
        id = id,
        category = NotificationCategory(categoryId, "task"),
        priority = NotificationPriority.PROMEMORIA_UTENTE,
        entityReference = EntityReference("task-1", "task"),
        title = "t",
        body = "b",
        scheduledFor = Instant.parse("2026-01-01T15:00:00Z"),
    )

    @Test
    fun `record then findById returns the request`() {
        val store = InMemoryNotificationHistoryStore()
        store.record(request("r1"))
        assertEquals("r1", store.findById("r1")?.id)
    }

    @Test
    fun `findById is null for an unrecorded id`() {
        assertNull(InMemoryNotificationHistoryStore().findById("never-recorded"))
    }

    @Test
    fun `recording the same id twice updates it in place, not duplicating it`() {
        val store = InMemoryNotificationHistoryStore()
        store.record(request("r1").copy(state = NotificationState.PIANIFICATA))
        store.record(request("r1").copy(state = NotificationState.MOSTRATA))

        assertEquals(1, store.recent(10).size)
        assertEquals(NotificationState.MOSTRATA, store.findById("r1")?.state)
    }

    @Test
    fun `recent returns most-recent-first, capped at the limit`() {
        val store = InMemoryNotificationHistoryStore()
        store.record(request("r1"))
        store.record(request("r2"))
        store.record(request("r3"))

        assertEquals(listOf("r3", "r2"), store.recent(2).map { it.id })
    }

    @Test
    fun `byCategory filters to only that category`() {
        val store = InMemoryNotificationHistoryStore()
        store.record(request("r1", categoryId = "task.reminder"))
        store.record(request("r2", categoryId = "habit.streak-risk"))

        assertEquals(listOf("r1"), store.byCategory("task.reminder").map { it.id })
    }
}
