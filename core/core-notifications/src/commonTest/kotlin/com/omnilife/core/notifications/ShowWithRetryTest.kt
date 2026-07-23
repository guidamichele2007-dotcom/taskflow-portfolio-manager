package com.omnilife.core.notifications

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private class FlakyLocalNotificationService(private val failuresBeforeSuccess: Int) : LocalNotificationService {
    var attempts = 0
        private set
    var delivered = false
        private set

    override fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    ) {
        attempts++
        if (attempts <= failuresBeforeSuccess) {
            throw IllegalStateException("simulated platform scheduling failure")
        }
        delivered = true
        onDelivered(request)
    }

    override fun cancel(requestId: String) = Unit
}

private class AlwaysFailingLocalNotificationService : LocalNotificationService {
    var attempts = 0
        private set

    override fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    ) {
        attempts++
        throw IllegalStateException("simulated platform scheduling failure")
    }

    override fun cancel(requestId: String) = Unit
}

class ShowWithRetryTest {
    private val request =
        NotificationRequest(
            id = "r1",
            category = NotificationCategory("task.reminder", "task"),
            priority = NotificationPriority.PROMEMORIA_UTENTE,
            entityReference = EntityReference("task-1", "task"),
            title = "t",
            body = "b",
            scheduledFor = Instant.parse("2026-01-01T10:00:00Z"),
        )
    private val channel = NotificationChannelSpec("task.reminder", "task", NotificationImportance.HIGH)

    @Test
    fun `showWithRetry succeeds after transient failures, using RetryEngine backoff`() =
        runTest {
            val service = FlakyLocalNotificationService(failuresBeforeSuccess = 2)

            service.showWithRetry(request, channel) {}

            assertEquals(3, service.attempts)
            assertEquals(true, service.delivered)
        }

    @Test
    fun `showWithRetry gives up once retries are exhausted`() =
        runTest {
            val service = AlwaysFailingLocalNotificationService()

            assertFailsWith<IllegalStateException> {
                service.showWithRetry(request, channel) {}
            }
            // 6 calls total: the initial attempt (0) plus 5 more before hasExhaustedRetries(5) trips.
            assertEquals(6, service.attempts)
        }
}
