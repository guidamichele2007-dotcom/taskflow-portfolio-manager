package com.omnilife.core.eventbus

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private data class SampleEvent(val id: String) : DomainEvent
private data class OtherEvent(val id: String) : DomainEvent

class InMemoryEventBusTest {
    @Test
    fun `delivers a published event to a subscribed listener`() {
        val bus = InMemoryEventBus()
        val received = mutableListOf<SampleEvent>()
        bus.subscribe(SampleEvent::class, EventSubscriber { received.add(it) })

        bus.publish(SampleEvent("a"))

        assertEquals(listOf(SampleEvent("a")), received)
    }

    @Test
    fun `never delivers an event to a subscriber of a different type`() {
        val bus = InMemoryEventBus()
        val received = mutableListOf<OtherEvent>()
        bus.subscribe(OtherEvent::class, EventSubscriber { received.add(it) })

        bus.publish(SampleEvent("a"))

        assertTrue(received.isEmpty())
    }

    @Test
    fun `delivers to every subscriber of the same type`() {
        val bus = InMemoryEventBus()
        var countA = 0
        var countB = 0
        bus.subscribe(SampleEvent::class, EventSubscriber { countA++ })
        bus.subscribe(SampleEvent::class, EventSubscriber { countB++ })

        bus.publish(SampleEvent("a"))

        assertEquals(1, countA)
        assertEquals(1, countB)
    }

    @Test
    fun `preserves publish order for a single producer`() {
        val bus = InMemoryEventBus()
        val received = mutableListOf<String>()
        bus.subscribe(SampleEvent::class, EventSubscriber { received.add(it.id) })

        bus.publish(SampleEvent("first"))
        bus.publish(SampleEvent("second"))
        bus.publish(SampleEvent("third"))

        assertEquals(listOf("first", "second", "third"), received)
    }

    @Test
    fun `a cancelled subscription stops receiving events`() {
        val bus = InMemoryEventBus()
        var count = 0
        val subscription = bus.subscribe(SampleEvent::class, EventSubscriber { count++ })

        bus.publish(SampleEvent("a"))
        subscription.cancel()
        bus.publish(SampleEvent("b"))

        assertEquals(1, count)
    }

    @Test
    fun `publishing with no subscribers is a silent no-op`() {
        val bus = InMemoryEventBus()
        bus.publish(SampleEvent("a"))
    }

    @Test
    fun `reified subscribe extension resolves the type token automatically`() {
        val bus = InMemoryEventBus()
        val received = mutableListOf<SampleEvent>()
        bus.subscribe<SampleEvent> { received.add(it) }

        bus.publish(SampleEvent("a"))

        assertEquals(1, received.size)
    }
}
