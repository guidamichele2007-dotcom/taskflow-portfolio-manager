package com.omnilife.core.eventbus

import kotlin.reflect.KClass

/**
 * Marker for anything published on the Bus Eventi (Technical Architecture
 * Bible §03). Payload must stay minimal by construction: entity id, type,
 * timestamp — never the full entity content (a consumer that needs content
 * reads it back from the owning module, never off the event itself).
 */
public interface DomainEvent

/** A single subscriber for one concrete [DomainEvent] subtype. */
public fun interface EventSubscriber<in E : DomainEvent> {
    public fun onEvent(event: E)
}

/** Handle returned by [EventBus.subscribe]; cancels that one subscription. */
public interface Subscription {
    public fun cancel()
}

/**
 * Bus Eventi (Technical Architecture Bible §03): local, in-memory,
 * publish/subscribe between domain modules with no direct dependency on
 * each other. Exactly the two conceptual ports the Bible defines — publish
 * and subscribe — nothing else (the Bible does not fix a concrete method
 * signature; this is the Sprint 1 convention, see README-BUILD.md §11).
 *
 * Delivery is synchronous ("operazione sincrona e locale, mai di rete",
 * §03 §4): [publish] returns only after every currently-subscribed handler
 * has run. Ordering is guaranteed per-producer (subscribers see events from
 * one publisher in the order it published them) but not across producers.
 */
public interface EventBus {
    public fun <E : DomainEvent> publish(event: E)

    public fun <E : DomainEvent> subscribe(eventType: KClass<E>, subscriber: EventSubscriber<E>): Subscription
}

public inline fun <reified E : DomainEvent> EventBus.subscribe(subscriber: EventSubscriber<E>): Subscription =
    subscribe(E::class, subscriber)
