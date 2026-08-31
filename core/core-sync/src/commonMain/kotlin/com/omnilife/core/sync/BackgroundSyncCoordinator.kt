package com.omnilife.core.sync

import com.omnilife.core.common.OmniResult
import kotlinx.datetime.Instant

/**
 * The transport a sync round hands one outbox item to — deliberately a
 * bare `fun interface` with no HTTP/gRPC/platform specifics. Providing a
 * concrete implementation (talking to the actual backend) is out of this
 * sprint's scope; [BackgroundSyncCoordinator] is verified against fakes.
 */
public fun interface RemoteSyncTransport {
    /** Throws on transport failure (network error, timeout); returns the server's accept/reject decision otherwise. */
    public suspend fun send(item: OutboxItem): RemoteAcceptance
}

public enum class RemoteAcceptance {
    ACCEPTED,
    REJECTED,
}

/**
 * Drives one attempt-all-eligible-items sync round: pulls from
 * [SyncOutboxStore] via [SyncScheduler], sends each through
 * [RemoteSyncTransport], and reports outcomes through [SyncStateManager]
 * — the "Background Sync" component (Technical Architecture Bible §05).
 * The actual OS-level trigger (WorkManager/BGTaskScheduler job,
 * connectivity callback) is unwritten app-shell wiring, same boundary as
 * [SyncScheduler].
 *
 * [RetryEngine] governs *when* a failed item becomes eligible again — this
 * coordinator does not sleep or schedule itself; a caller (platform job
 * scheduler) re-invokes [runOnce] on its own cadence, and
 * [RetryEngine.delayForAttempt] tells it how soon that's worth doing.
 */
public class BackgroundSyncCoordinator(
    private val outbox: SyncOutboxStore,
    private val scheduler: SyncScheduler,
    private val networkMonitor: NetworkMonitor,
    private val stateManager: SyncStateManager,
    private val transport: RemoteSyncTransport,
) {
    private val attemptCounts = mutableMapOf<String, Int>()
    private val firstFailureAt = mutableMapOf<String, Instant>()

    /**
     * Attempts to sync every currently-eligible item once, in priority
     * order, stopping at the first item whose retry backoff hasn't
     * elapsed or the moment the queue is empty. Never throws — every
     * failure is captured as an [OmniResult.Failure] and reflected in
     * [SyncStateManager]; a caller loop can keep calling [runOnce] safely.
     * [now] is wall-clock time, used only for [RetryEngine]'s
     * persistent-failure threshold and the status timestamp — never for
     * ordering decisions (those stay on [LogicalTimestamp]).
     */
    public suspend fun runOnce(now: Instant): OmniResult<Int> {
        if (!networkMonitor.isOnline()) {
            stateManager.transitionTo(SyncPhase.OFFLINE)
            return OmniResult.Success(0)
        }

        stateManager.transitionTo(SyncPhase.SYNCING)
        var syncedCount = 0

        while (true) {
            val item = scheduler.nextEligibleItem() ?: break

            val outcome =
                try {
                    transport.send(item)
                } catch (
                    @Suppress("TooGenericExceptionCaught") transportError: Exception,
                ) {
                    // Deliberately broad: RemoteSyncTransport is an unconstrained caller-supplied
                    // implementation (a real one would wrap platform networking), so any exception
                    // it throws must become a typed SyncError, never crash the sync round.
                    recordFailure(item.id, now)
                    val error = SyncError.TransportFailure(transportError.message ?: "unknown")
                    stateManager.recordError(error.message, outbox.size())
                    return OmniResult.Failure(error)
                }

            when (outcome) {
                RemoteAcceptance.ACCEPTED -> {
                    // Idempotent: acknowledging an id already removed (e.g. a duplicate
                    // delivery after a retry) is a no-op, never an error.
                    outbox.acknowledge(item.id)
                    attemptCounts.remove(item.id)
                    firstFailureAt.remove(item.id)
                    syncedCount++
                }
                RemoteAcceptance.REJECTED -> {
                    recordFailure(item.id, now)
                    val error = SyncError.RemoteRejected(item.id, "server rejected item")
                    stateManager.recordError(error.message, outbox.size())
                    return OmniResult.Failure(error)
                }
            }
        }

        stateManager.recordSuccess(now, outbox.size())
        return OmniResult.Success(syncedCount)
    }

    /** Whether [itemId] has failed long enough that automatic retry is no longer appropriate (MFC §3). */
    public fun hasPersistentFailure(
        itemId: String,
        now: Instant,
    ): Boolean {
        val firstFailure = firstFailureAt[itemId] ?: return false
        return RetryEngine.hasPersistentFailure(firstFailure, now)
    }

    private fun recordFailure(
        itemId: String,
        now: Instant,
    ) {
        attemptCounts[itemId] = (attemptCounts[itemId] ?: 0) + 1
        firstFailureAt.putIfAbsent(itemId, now)
    }
}
