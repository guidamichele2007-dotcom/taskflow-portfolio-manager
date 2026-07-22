package com.omnilife.core.sync

/**
 * The cross-device idempotency key Data Model Bible §11 §3 mandates for
 * recurring occurrences: "la chiave logica è (regola_ricorrenza_id,
 * periodo), non il timestamp di esecuzione". This is the exact gap
 * `domain-task`'s Sprint 1 report flagged as TASK-AC-05 and Sprint 1/2
 * reviews listed as the Sync Engine blocker — this type and store are the
 * ready-to-adopt mechanism; wiring `domain-task`'s `CompleteTask` use case
 * to use it is explicitly out of this sprint's scope (only the four Core
 * Platform modules) and remains a Sprint 4 item (see sprint3_report.md).
 */
public data class RecurrenceOccurrenceKey(public val recurrenceRuleId: String, public val period: String)

/** Tracks which occurrences have already been generated, regardless of which device generated them. */
public interface RecurrenceOccurrenceStore {
    /** Returns `true` if this is the first time [key] is seen (and records it); `false` if already generated. */
    public fun markGeneratedIfAbsent(key: RecurrenceOccurrenceKey): Boolean
}

public class InMemoryRecurrenceOccurrenceStore : RecurrenceOccurrenceStore {
    private val seen = mutableSetOf<RecurrenceOccurrenceKey>()

    override fun markGeneratedIfAbsent(key: RecurrenceOccurrenceKey): Boolean = seen.add(key)
}
