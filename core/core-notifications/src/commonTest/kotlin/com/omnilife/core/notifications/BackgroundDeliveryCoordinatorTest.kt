package com.omnilife.core.notifications

import com.omnilife.core.eventbus.InMemoryEventBus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

private class RecordingLocalNotificationService : LocalNotificationService {
    val shown = mutableListOf<NotificationRequest>()

    override fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    ) {
        shown += request
        onDelivered(request.copy(state = NotificationState.MOSTRATA, outcome = NotificationOutcome.MOSTRATA))
    }

    override fun cancel(requestId: String) = Unit
}

class BackgroundDeliveryCoordinatorTest {
    private val zone = TimeZone.UTC
    private val category = NotificationCategory("task.reminder", "task")

    private fun request(
        id: String,
        scheduledFor: Instant,
    ) = NotificationRequest(
        id = id,
        category = category,
        priority = NotificationPriority.PROMEMORIA_UTENTE,
        entityReference = EntityReference("task-1", "task"),
        title = "t",
        body = "b",
        scheduledFor = scheduledFor,
    )

    @Test
    fun `runOnce delivers a quiet-hours-deferred request once wake time arrives`() {
        val service = RecordingLocalNotificationService()
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val broker =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(),
                digest = InMemoryNotificationDigest(),
                localNotificationService = service,
                eventBus = InMemoryEventBus(),
            )
        val coordinator = BackgroundDeliveryCoordinator(broker)
        // 05:00 is still within the default 22-8 quiet window; waking at 08:00 (window end,
        // exclusive) is only 3h later — within SmartRescheduler's 4h relevance window (TDR-29).
        val nightTime = Instant.parse("2026-01-02T05:00:00Z")
        broker.request(request("r1", nightTime), nightTime, zone)

        coordinator.runOnce(Instant.parse("2026-01-02T08:00:00Z"), zone)

        assertEquals(listOf("r1"), service.shown.map { it.id })
    }

    @Test
    fun `runOnce also flushes a due digest in the same pass`() {
        val service = RecordingLocalNotificationService()
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val broker =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(initialDailyLimit = 0),
                digest = InMemoryNotificationDigest(),
                localNotificationService = service,
                eventBus = InMemoryEventBus(),
                digestDeliveryHour = 18,
            )
        val coordinator = BackgroundDeliveryCoordinator(broker)
        val morning = Instant.parse("2026-01-01T10:00:00Z")
        broker.request(request("r1", morning), morning, zone)

        coordinator.runOnce(Instant.parse("2026-01-01T19:00:00Z"), zone)

        assertEquals(1, service.shown.size)
    }
}
