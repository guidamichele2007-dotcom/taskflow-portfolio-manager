package com.omnilife.feature.search

import com.omnilife.core.search.SearchFilter
import com.omnilife.core.search.SearchResult

/**
 * MVI state for IA-100/101 (Global Search, SRCH-001…006). Search itself is synchronous
 * (`UnifiedSearchService.search` — local FTS5, no network, TDR-25), so there is no loading state
 * to model: every query change resolves immediately, same as Home's `GlobalSearchEntry`.
 */
public data class SearchUiState(
    val query: String = "",
    val filter: SearchFilter = SearchFilter(),
    val results: List<SearchResult> = emptyList(),
    val recentSearches: List<String> = emptyList(),
) {
    /** MUC §6: "never used" (no query yet) vs "filtered to nothing" are visually distinct states. */
    public val isEmptyResult: Boolean get() = query.isNotBlank() && results.isEmpty()
}
