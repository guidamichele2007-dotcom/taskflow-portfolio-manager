package com.omnilife.core.sync

/**
 * Facade composing this module's components (Functional Bible MFC §3; Data
 * Model Bible §8/§11; Technical Architecture Bible §05; TDR-05/24) — a
 * single point a future app entry point wires up. Each component (the CRDT
 * types, [SyncOutboxStore], [RetryEngine], [SyncScheduler], [VersionManager],
 * [ChangeTracker], [MergeEngine], [NetworkMonitor], [SyncStateManager]) also
 * works standalone; nothing here depends on any `domain-*` module (out of
 * this sprint's scope to wire up — see sprint3_report.md).
 *
 * [BackgroundSyncCoordinator] is deliberately **not** constructed here: it
 * requires a [RemoteSyncTransport], and providing a concrete transport
 * (the actual network call to a backend) is out of this sprint's scope —
 * a caller assembles one from these pieces once a transport exists.
 */
public class SyncEngine(
    public val outbox: SyncOutboxStore = InMemorySyncOutboxStore(),
    public val scheduler: SyncScheduler = SyncScheduler(outbox),
    public val recurrenceOccurrences: RecurrenceOccurrenceStore = InMemoryRecurrenceOccurrenceStore(),
    public val versionManager: VersionManager = InMemoryVersionManager(),
    public val changeTracker: ChangeTracker = InMemoryChangeTracker(),
    public val mergeEngine: MergeEngine = MergeEngine(),
    public val networkMonitor: NetworkMonitor = ManualNetworkMonitor(),
    public val stateManager: SyncStateManager = InMemorySyncStateManager(),
)
