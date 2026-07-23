package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationDigestTest {
    private fun request(id: String) =
        NotificationRequest(
            id = id,
            category = NotificationCategory("task.reminder", "task"),
            priority = NotificationPriority.UTILE,
            entityReference = EntityReference("task-1", "task"),
            title = "t",
            body = "b",
            scheduledFor = Instant.parse("2026-01-01T10:00:00Z"),
        )

    @Test
    fun `addToDigest accumulates pendingCount`() {
        val digest = InMemoryNotificationDigest()
        digest.addToDigest(request("a"))
        digest.addToDigest(request("b"))

        assertEquals(2, digest.pendingCount())
    }

    @Test
    fun `flush empties the digest and returns everything accumulated`() {
        val digest = InMemoryNotificationDigest()
        digest.addToDigest(request("a"))
        digest.addToDigest(request("b"))

        val flushed = digest.flush()

        assertEquals(listOf("a", "b"), flushed.map { it.id })
        assertEquals(0, digest.pendingCount())
    }

    @Test
    fun `flushing an empty digest returns an empty list, not an error`() {
        val digest = InMemoryNotificationDigest()
        assertTrue(digest.flush().isEmpty())
    }
}
