package com.omnilife.core.sync

import kotlinx.datetime.Instant

/** Observable machine-readable sync status (Technical Architecture Bible §05). */
public enum class SyncPhase {
    IDLE,
    SYNCING,
    OFFLINE,
    ERROR,
}

/**
 * [lastSuccessfulSyncAt] is wall-clock [Instant], unlike [LogicalTimestamp]
 * elsewhere in this module — this field is for human-readable status
 * display ("synced 2 minutes ago"), never for CRDT ordering decisions, so
 * the module's "never wall-clock for ordering" rule (LogicalTimestamp's
 * own doc, MFC-E-10) doesn't apply here.
 */
public data class SyncState(
    public val phase: SyncPhase,
    public val pendingCount: Int,
    public val lastSuccessfulSyncAt: Instant?,
    public val lastError: String?,
)

/**
 * Single place every module can ask "what is sync doing right now" —
 * e.g. for a future status indicator UI this sprint does not build
 * (explicitly out of scope: "Non implementare: UI").
 */
public interface SyncStateManager {
    public fun current(): SyncState

    public fun transitionTo(phase: SyncPhase)

    public fun recordSuccess(
        at: Instant,
        pendingCount: Int,
    )

    public fun recordError(
        message: String,
        pendingCount: Int,
    )

    public fun observe(listener: (SyncState) -> Unit)
}

public class InMemorySyncStateManager : SyncStateManager {
    private var state = SyncState(SyncPhase.IDLE, pendingCount = 0, lastSuccessfulSyncAt = null, lastError = null)
    private val listeners = mutableListOf<(SyncState) -> Unit>()

    override fun current(): SyncState = state

    override fun transitionTo(phase: SyncPhase) {
        state = state.copy(phase = phase)
        notifyListeners()
    }

    override fun recordSuccess(
        at: Instant,
        pendingCount: Int,
    ) {
        state =
            state.copy(
                phase = SyncPhase.IDLE,
                lastSuccessfulSyncAt = at,
                lastError = null,
                pendingCount = pendingCount,
            )
        notifyListeners()
    }

    override fun recordError(
        message: String,
        pendingCount: Int,
    ) {
        state = state.copy(phase = SyncPhase.ERROR, lastError = message, pendingCount = pendingCount)
        notifyListeners()
    }

    override fun observe(listener: (SyncState) -> Unit) {
        listeners.add(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it(state) }
    }
}
