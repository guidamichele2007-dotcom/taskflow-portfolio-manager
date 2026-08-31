package com.omnilife.feature.search

import com.omnilife.core.search.SearchFilter

/** User intents for IA-100/101 (Global Search). */
public sealed interface SearchIntent {
    public data class ChangeQuery(val query: String) : SearchIntent

    public data class ChangeFilter(val filter: SearchFilter) : SearchIntent

    public data class SelectRecentSearch(val query: String) : SearchIntent

    public data class RemoveRecentSearch(val query: String) : SearchIntent

    public data object ClearRecentSearches : SearchIntent

    /** SRCH-004: a result the user actually opened is what earns a place in recent searches. */
    public data object ResultOpened : SearchIntent
}
