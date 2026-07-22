package com.omnilife.core.sync

/**
 * Observed-Remove Set (TDR-24, Data Model Bible §11 §6): the CRDT for
 * GraphLink-style reference sets (INV-04 — concurrent adds always
 * accumulate, never lost; concurrent removes apply regardless of arrival
 * order). Each add is tagged with a unique [LogicalTimestamp]; a remove
 * tombstones every tag the removing device had observed for that element —
 * an element the same or another device re-adds afterwards gets a new tag
 * and is present again, which is the whole point of "observed remove"
 * (distinguishing it from a naive 2P-set that can never re-add).
 */
public class ORSet<T> private constructor(
    private val addedTags: Map<T, Set<LogicalTimestamp>>,
    private val removedTags: Set<LogicalTimestamp>,
) {
    public fun elements(): Set<T> = addedTags.filterValues { tags -> tags.any { it !in removedTags } }.keys

    public fun contains(element: T): Boolean = element in elements()

    public fun add(
        element: T,
        tag: LogicalTimestamp,
    ): ORSet<T> {
        val tags = addedTags[element].orEmpty() + tag
        return ORSet(addedTags + (element to tags), removedTags)
    }

    /**
     * Tombstones every tag currently observed for [element] — a
     * concurrently-added new tag for the same element survives.
     */
    public fun remove(element: T): ORSet<T> {
        val observedTags = addedTags[element].orEmpty()
        return ORSet(addedTags, removedTags + observedTags)
    }

    /** Commutative, idempotent union (verified by [ORSetConvergenceTest]) — the actual CRDT merge. */
    public fun merge(other: ORSet<T>): ORSet<T> {
        val mergedAdds =
            (addedTags.keys + other.addedTags.keys).associateWith { element ->
                addedTags[element].orEmpty() + other.addedTags[element].orEmpty()
            }
        return ORSet(mergedAdds, removedTags + other.removedTags)
    }

    public companion object {
        public fun <T> empty(): ORSet<T> = ORSet(emptyMap(), emptySet())

        /**
         * Bulk construction from an already-built add-tag map (e.g.
         * rehydrating from storage, or a benchmark input) — O(1), unlike
         * building the same state via repeated [add] calls (each of which
         * copies the whole backing map, O(n) per call).
         */
        public fun <T> fromAddedTags(addedTags: Map<T, Set<LogicalTimestamp>>): ORSet<T> = ORSet(addedTags, emptySet())
    }
}
