package com.omnilife.core.search

import kotlinx.datetime.Instant

/**
 * The generic contract every module's entities present to `core-search`
 * (Technical Architecture Bible §13 §4: the Search service "opera su un
 * contratto generico 'entità indicizzabile'" — no dependency on
 * `domain-task` or any other `domain-*` module, by design). A future
 * domain module maps its own entity to this shape when indexing; that
 * wiring is out of this sprint's scope (only the four Core Platform
 * modules).
 */
public interface IndexableEntity {
    public val id: String
    public val entityType: String
    public val title: String
    public val content: String?
    public val category: String?
    public val lifecycleState: String
    public val createdAt: Instant
    public val modifiedAt: Instant
}

/** A plain-data implementation for tests/benchmarks and any caller that doesn't need a custom class. */
public data class SimpleIndexableEntity(
    override val id: String,
    override val entityType: String,
    override val title: String,
    override val content: String? = null,
    override val category: String? = null,
    override val lifecycleState: String = "ACTIVE",
    override val createdAt: Instant,
    override val modifiedAt: Instant,
) : IndexableEntity
