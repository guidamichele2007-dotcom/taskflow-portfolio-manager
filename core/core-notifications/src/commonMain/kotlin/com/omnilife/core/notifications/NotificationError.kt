package com.omnilife.core.notifications

import com.omnilife.core.common.DomainError

/** `core-notifications` errors (TDR-21 convention). */
public sealed class NotificationError(override val message: String) : DomainError {
    public data class CategoryDisabled(val categoryId: String) :
        NotificationError("La categoria '$categoryId' è disattivata dall'utente")

    public data class RequestNotFound(val requestId: String) :
        NotificationError("Nessuna richiesta di notifica con id '$requestId'")

    public data object PermissionDenied :
        NotificationError("Permesso di sistema per le notifiche non concesso")
}
