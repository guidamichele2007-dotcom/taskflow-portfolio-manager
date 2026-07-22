package com.omnilife.core.search

import kotlinx.datetime.Instant

/** One matched entity (SRCH-001). [content] is never included verbatim — only what the ranking needs. */
public data class SearchResult(
    public val id: String,
    public val entityType: String,
    public val title: String,
    public val category: String?,
    public val lifecycleState: String,
    public val modifiedAt: Instant,
)

/**
 * SRCH-002/003: type filter + contextual filters. [entityType] is the
 * closed-per-active-module chip (SRCH-002); [includeArchivedOrTrashed] is
 * SRCH-006's explicit opt-in (default excluded, INV-14 — archived entities
 * stay indexed but are never surfaced without it).
 */
public data class SearchFilter(
    public val entityType: String? = null,
    public val category: String? = null,
    public val includeArchivedOrTrashed: Boolean = false,
)

/**
 * SRCH-001: full-text search across every active module's indexed
 * entities, entirely local (SRCH-R-02: "nessuna ricerca server-side, mai").
 */
public interface UnifiedSearchService {
    public fun search(
        query: String,
        filter: SearchFilter = SearchFilter(),
    ): List<SearchResult>
}
