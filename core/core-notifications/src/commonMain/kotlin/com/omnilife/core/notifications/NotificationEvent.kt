package com.omnilife.core.notifications

import com.omnilife.core.eventbus.DomainEvent
import kotlinx.datetime.Instant

/**
 * NTF §3 events: "sottoscrive le richieste dei moduli (`ntf.request` con categoria); pubblica
 * `ntf.action.performed`" — named `NtfRequested`/`NtfActionPerformed` per TDR-26's already-recorded
 * contract. Any module publishes [NtfRequested] instead of notifying directly (NTF-001); the
 * broker publishes [NtfActionPerformed] so the *owning* module (never this one) executes the
 * actual complete/postpone/check (NTF-005) — this module never imports a domain-* type.
 */
public sealed interface NotificationEvent : DomainEvent {
    public data class NtfRequested(val request: NotificationRequest, val at: Instant) : NotificationEvent

    public data class NtfActionPerformed(
        val entityReference: EntityReference,
        val actionId: String,
        val at: Instant,
    ) : NotificationEvent
}
