package com.omnilife.core.notifications

import com.omnilife.core.eventbus.EventSubscriber
import com.omnilife.core.eventbus.InMemoryEventBus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeLocalNotificationService : LocalNotificationService {
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

class NotificationBrokerTest {
    private val zone = TimeZone.UTC
    private val category = NotificationCategory("task.reminder", "task")

    private fun request(
        id: String,
        priority: NotificationPriority = NotificationPriority.UTILE,
        scheduledFor: Instant = Instant.parse("2026-01-01T10:00:00Z"),
    ) = NotificationRequest(
        id = id,
        category = category,
        priority = priority,
        entityReference = EntityReference("task-1", "task"),
        title = "t",
        body = "b",
        scheduledFor = scheduledFor,
    )

    private fun broker(
        localService: LocalNotificationService = FakeLocalNotificationService(),
        digestDeliveryHour: Int = 18,
        burstThreshold: Int = 50,
    ): NotificationBroker {
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        return NotificationBroker(
            categoryRegistry = registry,
            historyStore = InMemoryNotificationHistoryStore(),
            budget = NotificationBudget(),
            digest = InMemoryNotificationDigest(),
            localNotificationService = localService,
            eventBus = InMemoryEventBus(),
            digestDeliveryHour = digestDeliveryHour,
            burstThreshold = burstThreshold,
        )
    }

    @Test
    fun `a request outside quiet hours and within budget is scheduled immediately`() {
        val service = FakeLocalNotificationService()
        val broker = broker(localService = service)

        val disposition = broker.request(request("r1"), Instant.parse("2026-01-01T10:00:00Z"), zone)

        assertEquals(NotificationDisposition.SCHEDULED_IMMEDIATELY, disposition)
        assertEquals(listOf("r1"), service.shown.map { it.id })
    }

    @Test
    fun `NTF-AC-01 - budget 3 allows 3 UTILE requests, the 4th and 5th route to digest`() {
        val service = FakeLocalNotificationService()
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val brokerInstance =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(initialDailyLimit = 3),
                digest = InMemoryNotificationDigest(),
                localNotificationService = service,
                eventBus = InMemoryEventBus(),
            )
        val now = Instant.parse("2026-01-01T10:00:00Z")

        val dispositions = (1..5).map { brokerInstance.request(request("r$it"), now, zone) }

        assertEquals(3, dispositions.count { it == NotificationDisposition.SCHEDULED_IMMEDIATELY })
        assertEquals(2, dispositions.count { it == NotificationDisposition.ROUTED_TO_DIGEST })
    }

    @Test
    fun `PROMEMORIA_UTENTE never routes to digest for budget reasons`() {
        val service = FakeLocalNotificationService()
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val brokerInstance =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(initialDailyLimit = 0),
                digest = InMemoryNotificationDigest(),
                localNotificationService = service,
                eventBus = InMemoryEventBus(),
            )

        val disposition =
            brokerInstance.request(
                request("r1", priority = NotificationPriority.PROMEMORIA_UTENTE),
                Instant.parse("2026-01-01T10:00:00Z"),
                zone,
            )

        assertEquals(NotificationDisposition.SCHEDULED_IMMEDIATELY, disposition)
    }

    @Test
    fun `a disabled category is suppressed, never shown and never digested`() {
        val service = FakeLocalNotificationService()
        val registry =
            InMemoryNotificationCategoryRegistry().apply {
                register(category)
                setEnabled(category.id, false)
            }
        val brokerInstance =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(),
                digest = InMemoryNotificationDigest(),
                localNotificationService = service,
                eventBus = InMemoryEventBus(),
            )

        val disposition = brokerInstance.request(request("r1"), Instant.parse("2026-01-01T10:00:00Z"), zone)

        assertEquals(NotificationDisposition.SUPPRESSED_CATEGORY_DISABLED, disposition)
        assertTrue(service.shown.isEmpty())
    }

    @Test
    fun `NTF-004 - a request during quiet hours is deferred, not shown`() {
        val service = FakeLocalNotificationService()
        val broker = broker(localService = service)

        val disposition = broker.request(request("r1"), Instant.parse("2026-01-01T23:00:00Z"), zone)

        assertEquals(NotificationDisposition.DEFERRED_FOR_QUIET_HOURS, disposition)
        assertTrue(service.shown.isEmpty())
    }

    @Test
    fun `NTF-AC-03 - a deferred request still relevant at wake time is shown`() {
        val service = FakeLocalNotificationService()
        val broker = broker(localService = service)
        // 05:00 is still within the default 22-8 quiet window; waking at 08:00 (window end,
        // exclusive) is only 3h later — within SmartRescheduler's 4h relevance window (TDR-29).
        val scheduledFor = Instant.parse("2026-01-02T05:00:00Z")

        broker.request(request("r1", scheduledFor = scheduledFor), scheduledFor, zone)
        broker.processDeferred(Instant.parse("2026-01-02T08:00:00Z"), zone)

        assertEquals(listOf("r1"), service.shown.map { it.id })
    }

    @Test
    fun `NTF-AC-03 - a deferred request no longer relevant at wake time is dropped, never shown`() {
        val service = FakeLocalNotificationService()
        val broker = broker(localService = service)
        val scheduledFor = Instant.parse("2026-01-01T23:00:00Z")

        broker.request(request("r1", scheduledFor = scheduledFor), scheduledFor, zone)
        broker.processDeferred(Instant.parse("2026-01-02T08:00:00Z"), zone)

        assertTrue(service.shown.isEmpty())
    }

    @Test
    fun `NTF §2 edge case - a burst beyond the threshold routes further requests to digest`() {
        val service = FakeLocalNotificationService()
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        // Ample budget (10) isolates the burst check from the budget check — this test verifies
        // burst alone, NTF-AC-01's test above already covers budget alone.
        val brokerInstance =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(initialDailyLimit = 10),
                digest = InMemoryNotificationDigest(),
                localNotificationService = service,
                eventBus = InMemoryEventBus(),
                burstThreshold = 5,
            )
        val now = Instant.parse("2026-01-01T10:00:00Z")

        val dispositions = (1..8).map { brokerInstance.request(request("r$it"), now, zone) }

        assertEquals(5, dispositions.count { it == NotificationDisposition.SCHEDULED_IMMEDIATELY })
        assertEquals(3, dispositions.count { it == NotificationDisposition.ROUTED_TO_DIGEST })
    }

    @Test
    fun `flushDigestIfDue delivers the digest once the delivery hour has passed, at most once per day`() {
        val service = FakeLocalNotificationService()
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val budgetedBroker =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(initialDailyLimit = 0),
                digest = InMemoryNotificationDigest(),
                localNotificationService = service,
                eventBus = InMemoryEventBus(),
                digestDeliveryHour = 18,
            )
        budgetedBroker.request(request("r1"), Instant.parse("2026-01-01T10:00:00Z"), zone)

        budgetedBroker.flushDigestIfDue(Instant.parse("2026-01-01T12:00:00Z"), zone)
        assertTrue(service.shown.isEmpty())

        budgetedBroker.flushDigestIfDue(Instant.parse("2026-01-01T19:00:00Z"), zone)
        assertEquals(1, service.shown.size)

        // A second wake later the same day must not re-deliver an already-flushed (now empty) digest.
        budgetedBroker.flushDigestIfDue(Instant.parse("2026-01-01T20:00:00Z"), zone)
        assertEquals(1, service.shown.size)
    }

    @Test
    fun `request publishes NtfRequested for every request regardless of disposition`() {
        val eventBus = InMemoryEventBus()
        val published = mutableListOf<NotificationEvent.NtfRequested>()
        eventBus.subscribe(NotificationEvent.NtfRequested::class, EventSubscriber { published.add(it) })
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val brokerInstance =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(),
                digest = InMemoryNotificationDigest(),
                localNotificationService = FakeLocalNotificationService(),
                eventBus = eventBus,
            )

        brokerInstance.request(request("r1"), Instant.parse("2026-01-01T10:00:00Z"), zone)

        assertEquals(1, published.size)
        assertEquals("r1", published.single().request.id)
    }

    @Test
    fun `recordOutcome updates history and feeds the category registry`() {
        val registry = InMemoryNotificationCategoryRegistry().apply { register(category) }
        val brokerInstance =
            NotificationBroker(
                categoryRegistry = registry,
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(),
                digest = InMemoryNotificationDigest(),
                localNotificationService = FakeLocalNotificationService(),
                eventBus = InMemoryEventBus(),
            )
        val r = request("r1")

        repeat(3) { brokerInstance.recordOutcome(r, NotificationOutcome.IGNORATA) }

        assertTrue(registry.shouldProposeAutoDisable("task.reminder"))
    }
}
