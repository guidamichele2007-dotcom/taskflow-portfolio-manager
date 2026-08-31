package com.omnilife.core.eventbus

import kotlin.reflect.KClass

/**
 * Default [EventBus] implementation: in-memory, synchronous, single
 * process/device (Technical Architecture Bible §03 §5 — "non persistito
 * come log, è un meccanismo di notifica, non un event-store").
 *
 * Not thread-safe by design: the Bible describes a single-device,
 * single-process notification mechanism, not a concurrent queue; callers on
 * a shared-mutable-state platform are expected to publish/subscribe from a
 * single logical thread (e.g. the main/UI dispatcher), consistent with the
 * synchronous delivery contract itself.
 */
public class InMemoryEventBus : EventBus {
    private val subscribersByType = mutableMapOf<KClass<out DomainEvent>, MutableList<EventSubscriber<*>>>()

    override fun <E : DomainEvent> publish(event: E) {
        val subscribers = subscribersByType[event::class] ?: return
        // Copy before iterating: a subscriber may cancel itself (or another)
        // during delivery without corrupting this dispatch pass.
        for (subscriber in subscribers.toList()) {
            @Suppress("UNCHECKED_CAST")
            (subscriber as EventSubscriber<E>).onEvent(event)
        }
    }

    override fun <E : DomainEvent> subscribe(
        eventType: KClass<E>,
        subscriber: EventSubscriber<E>,
    ): Subscription {
        val subscribers = subscribersByType.getOrPut(eventType) { mutableListOf() }
        subscribers.add(subscriber)
        return object : Subscription {
            override fun cancel() {
                subscribersByType[eventType]?.remove(subscriber)
            }
        }
    }
}
