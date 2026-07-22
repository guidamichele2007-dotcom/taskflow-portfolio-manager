package com.omnilife.core.sync

import com.omnilife.core.common.DomainError

/**
 * `core-sync` errors (TDR-21 convention) — the vocabulary its operations
 * return in [com.omnilife.core.common.OmniResult.Failure].
 */
public sealed class SyncError(override val message: String) : DomainError {
    /** The transport a [BackgroundSyncCoordinator] calls threw or returned a network-level failure. */
    public data class TransportFailure(val reason: String) : SyncError("Trasporto di sincronizzazione fallito: $reason")

    /** The remote server explicitly rejected an item (as opposed to a transient transport failure). */
    public data class RemoteRejected(val itemId: String, val reason: String) :
        SyncError("Il server ha rifiutato l'elemento '$itemId': $reason")

    /** [RetryEngine.hasPersistentFailure] tripped — retrying automatically is no longer appropriate. */
    public data class PersistentFailure(val itemId: String) :
        SyncError("Fallimento persistente per l'elemento '$itemId', richiesto intervento manuale")
}
