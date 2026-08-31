package com.omnilife.core.common

import kotlinx.datetime.Instant

/** Stable identifier for any entity. Globally unique, never reassigned (INV-01). */
public typealias EntityId = String

/**
 * Common envelope shared by every user entity (Data Model Bible §00 §3,
 * MDEC-01). Composed as a field on each entity's data class — never
 * flattened/inherited — matching the Bible's own table, which lists
 * `campi_specifici` as one row alongside the envelope fields, not a
 * subclass relationship.
 */
public data class Envelope(
    val id: EntityId,
    val ownerAccountId: String,
    val schemaVersion: Int,
    val createdAt: Instant,
    val createdByDevice: String,
    val modifiedAt: Instant,
    val modifiedByDevice: String,
    val lifecycleState: EntityLifecycleState = EntityLifecycleState.ACTIVE,
    /** Set only when [lifecycleState] is TRASHED; determines the 30-day expiry (MFC-R-10, INV-06). */
    val trashedAt: Instant? = null,
)
