package com.omnilife.core.notifications

import com.omnilife.core.eventbus.EventSubscriber
import com.omnilife.core.eventbus.InMemoryEventBus
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NotificationActionDispatcherTest {
    private val entityReference = EntityReference("task-1", "task")
    private val request =
        NotificationRequest(
            id = "r1",
            category = NotificationCategory("task.reminder", "task"),
            priority = NotificationPriority.PROMEMORIA_UTENTE,
            entityReference = entityReference,
            title = "t",
            body = "b",
            scheduledFor = Instant.parse("2026-01-01T15:00:00Z"),
            actions = listOf(NotificationActionDescriptor("complete", "Completa")),
        )

    @Test
    fun `dispatch publishes NtfActionPerformed with the entity reference and action id`() {
        val eventBus = InMemoryEventBus()
        val published = mutableListOf<NotificationEvent.NtfActionPerformed>()
        eventBus.subscribe(NotificationEvent.NtfActionPerformed::class, EventSubscriber { published.add(it) })
        val dispatcher = NotificationActionDispatcher(eventBus)

        dispatcher.dispatch(request, "complete", Instant.parse("2026-01-01T15:05:00Z"))

        assertEquals(1, published.size)
        assertEquals(entityReference, published.single().entityReference)
        assertEquals("complete", published.single().actionId)
    }

    @Test
    fun `dispatch marks the request AZIONATA`() {
        val eventBus = InMemoryEventBus()
        val dispatcher = NotificationActionDispatcher(eventBus)

        val result = dispatcher.dispatch(request, "complete", Instant.parse("2026-01-01T15:05:00Z"))

        assertEquals(NotificationState.AZIONATA, result.state)
        assertEquals(NotificationOutcome.AZIONATA, result.outcome)
    }

    @Test
    fun `dispatching an action not declared on the request throws`() {
        val dispatcher = NotificationActionDispatcher(InMemoryEventBus())
        assertFailsWith<IllegalArgumentException> {
            dispatcher.dispatch(request, "not-declared", Instant.parse("2026-01-01T15:05:00Z"))
        }
    }
}
