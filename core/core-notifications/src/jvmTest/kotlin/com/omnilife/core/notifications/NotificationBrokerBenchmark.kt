package com.omnilife.core.notifications

import com.omnilife.core.eventbus.InMemoryEventBus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.system.measureNanoTime
import kotlin.test.Test

class NotificationBrokerBenchmark {
    private class NoOpLocalNotificationService : LocalNotificationService {
        override fun show(
            request: NotificationRequest,
            channel: NotificationChannelSpec,
            onDelivered: (NotificationRequest) -> Unit,
        ) = onDelivered(request)

        override fun cancel(requestId: String) = Unit
    }

    @Test
    fun `benchmark - NotificationBroker request() throughput for 10,000 requests`() {
        val category = NotificationCategory("task.reminder", "task")
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val broker =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(initialDailyLimit = NotificationBudget.MAX_DAILY_LIMIT),
                digest = InMemoryNotificationDigest(),
                localNotificationService = NoOpLocalNotificationService(),
                eventBus = InMemoryEventBus(),
                burstThreshold = Int.MAX_VALUE,
            )
        val zone = TimeZone.UTC
        val now = Instant.parse("2026-01-01T10:00:00Z")
        val requestCount = 10_000

        val elapsedNanos =
            measureNanoTime {
                repeat(requestCount) { i ->
                    broker.request(
                        NotificationRequest(
                            id = "r$i",
                            category = category,
                            priority = NotificationPriority.PROMEMORIA_UTENTE,
                            entityReference = EntityReference("task-$i", "task"),
                            title = "t$i",
                            body = "b$i",
                            scheduledFor = now,
                        ),
                        now,
                        zone,
                    )
                }
            }

        println(
            "[benchmark] NotificationBroker.request: $requestCount requests in ${elapsedNanos / 1_000_000}ms " +
                "(${"%.2f".format(requestCount / (elapsedNanos / 1_000_000_000.0))} requests/s)",
        )
    }
}
