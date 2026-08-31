package com.omnilife.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniChip
import com.omnilife.core.designsystem.components.OmniChipVariant
import com.omnilife.core.designsystem.components.OmniEmptyState
import com.omnilife.core.designsystem.components.OmniListItem
import com.omnilife.core.designsystem.components.OmniSearchField
import com.omnilife.core.designsystem.components.OmniTopBar
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.search.SearchResult

/** IA-100/101 (Global Search, SRCH-001…006). Stateless — mirrors every other L1 screen's boundary. */
@Composable
public fun SearchScreen(
    state: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    onResultClick: (SearchResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OmniTopBar(title = "Cerca")
        OmniSearchField(
            query = state.query,
            onQueryChange = { onIntent(SearchIntent.ChangeQuery(it)) },
            placeholder = "Cerca in tutti i moduli",
            resultCount = if (state.query.isNotBlank()) state.results.size else null,
        )
        when {
            state.query.isBlank() -> RecentSearchesSection(state.recentSearches, onIntent)
            state.isEmptyResult ->
                OmniEmptyState(
                    icon = OmniIconType.SEARCH,
                    message = "Nessun risultato per \"${state.query}\"",
                    actionLabel = "Cancella ricerca",
                    onActionClick = { onIntent(SearchIntent.ChangeQuery("")) },
                    modifier = Modifier.fillMaxSize(),
                )

            else ->
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    state.results.forEach { result ->
                        OmniListItem(
                            title = result.title,
                            secondaryText = result.entityType,
                            onClick = {
                                onIntent(SearchIntent.ResultOpened)
                                onResultClick(result)
                            },
                        )
                    }
                }
        }
    }
}

@Composable
private fun RecentSearchesSection(
    recentSearches: List<String>,
    onIntent: (SearchIntent) -> Unit,
) {
    if (recentSearches.isEmpty()) return
    Column {
        recentSearches.forEach { query ->
            OmniChip(
                text = query,
                selected = false,
                onClick = { onIntent(SearchIntent.SelectRecentSearch(query)) },
                onRemove = { onIntent(SearchIntent.RemoveRecentSearch(query)) },
                variant = OmniChipVariant.FILTRO,
            )
        }
    }
}
