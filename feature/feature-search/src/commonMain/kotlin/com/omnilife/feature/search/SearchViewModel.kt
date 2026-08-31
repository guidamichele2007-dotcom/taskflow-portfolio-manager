package com.omnilife.feature.search

import com.omnilife.core.search.RecentSearchStore
import com.omnilife.core.search.SearchFilter
import com.omnilife.core.search.UnifiedSearchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * MVI store for IA-100/101 (TDR-02). Pure Kotlin — no Compose dependency, fully unit-testable on
 * the JVM target. Verifies the real, already-indexed data (SRCH-001…006): task creation, editing,
 * completion, and trashing/archiving are reflected only insofar as `TaskSearchIndexBridge`
 * (`feature-task`) keeps the index consistent — this ViewModel only reads [searchService].
 */
public class SearchViewModel(
    private val searchService: UnifiedSearchService,
    private val recentSearchStore: RecentSearchStore,
) {
    private val _state = MutableStateFlow(SearchUiState(recentSearches = recentSearchStore.recent()))
    public val state: StateFlow<SearchUiState> = _state.asStateFlow()

    public fun dispatch(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.ChangeQuery -> runQuery(intent.query, _state.value.filter)

            is SearchIntent.ChangeFilter -> {
                _state.update { it.copy(filter = intent.filter) }
                runQuery(_state.value.query, intent.filter)
            }

            is SearchIntent.SelectRecentSearch -> runQuery(intent.query, _state.value.filter)

            is SearchIntent.RemoveRecentSearch -> {
                recentSearchStore.clear(intent.query)
                _state.update { it.copy(recentSearches = recentSearchStore.recent()) }
            }

            SearchIntent.ClearRecentSearches -> {
                recentSearchStore.clearAll()
                _state.update { it.copy(recentSearches = recentSearchStore.recent()) }
            }

            SearchIntent.ResultOpened -> {
                recentSearchStore.record(_state.value.query)
                _state.update { it.copy(recentSearches = recentSearchStore.recent()) }
            }
        }
    }

    private fun runQuery(
        query: String,
        filter: SearchFilter,
    ) {
        if (query.isBlank()) {
            _state.update { it.copy(query = query, results = emptyList()) }
            return
        }
        val results = searchService.search(query, filter)
        _state.update { it.copy(query = query, results = results) }
    }
}
