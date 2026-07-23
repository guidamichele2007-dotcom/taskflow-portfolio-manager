package com.omnilife.core.notifications

import com.omnilife.core.common.EntityId
import kotlinx.datetime.Instant

/** promemoria_utente never consumes budget (NTF-002); utile/informativa do. */
public enum class NotificationPriority {
    PROMEMORIA_UTENTE,
    UTILE,
    INFORMATIVA,
}

/** DM-NTF-01: pianificata → mostrata/azionata/ignorata, or scaduta_di_significato (NTF-004). */
public enum class NotificationState {
    PIANIFICATA,
    MOSTRATA,
    AZIONATA,
    IGNORATA,
    SCADUTA_DI_SIGNIFICATO,
}

/** DM-NTF-01 `esito`: what actually happened to a shown notification, feeds NTF-006. */
public enum class NotificationOutcome {
    MOSTRATA,
    AZIONATA,
    IGNORATA,
    ASSORBITA_IN_DIGEST,
}

/**
 * Read-only pointer to the entity that originated a request (DM-NTF-01 `entita_riferimento`) —
 * never the content itself.
 */
public data class EntityReference(public val entityId: EntityId, public val entityType: String)

/**
 * NTF-005: complete/postpone/check from the notification itself, without opening the app —
 * deliberately generic (a label + an opaque [actionId] the owning module interprets), never a
 * hardcoded Task/Habit-specific action here (this module has zero domain-* dependencies).
 */
public data class NotificationActionDescriptor(public val actionId: String, public val label: String)

/**
 * DM-NTF-01 · NotificationRequest — "ogni modulo *richiede* una notifica al broker centrale
 * (NTF-001); nessun modulo notifica direttamente." [category] is per-module/per-type
 * (NTF-007's granular toggle), [scheduledFor] is the intended fire time (subject to timezone
 * handling, quiet hours, and budget/digest before actually being shown).
 */
public data class NotificationRequest(
    public val id: String,
    public val category: NotificationCategory,
    public val priority: NotificationPriority,
    public val entityReference: EntityReference,
    public val title: String,
    public val body: String,
    public val scheduledFor: Instant,
    public val actions: List<NotificationActionDescriptor> = emptyList(),
    public val deepLink: String? = null,
    public val state: NotificationState = NotificationState.PIANIFICATA,
    public val outcome: NotificationOutcome? = null,
)
