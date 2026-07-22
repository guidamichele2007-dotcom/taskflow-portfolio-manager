package com.omnilife.core.sync

/**
 * Facade composing this module's components (Functional Bible MFC §3; Data
 * Model Bible §8/§11; Technical Architecture Bible §05; TDR-05/24) — a
 * single point a future app entry point wires up. Each component (the CRDT
 * types, [SyncOutboxStore], [RetryEngine], [SyncScheduler]) also works
 * standalone; nothing here depends on any `domain-*` module (out of this
 * sprint's scope to wire up — see sprint3_report.md).
 */
public class SyncEngine(
    public val outbox: SyncOutboxStore = InMemorySyncOutboxStore(),
    public val scheduler: SyncScheduler = SyncScheduler(outbox),
    public val recurrenceOccurrences: RecurrenceOccurrenceStore = InMemoryRecurrenceOccurrenceStore(),
)
