package com.omnilife.core.search

import com.omnilife.core.search.persistence.DatabaseDriverFactory
import com.omnilife.core.search.persistence.SqlDelightSearchIndex

/**
 * Facade composing this module's components (Functional Bible SRCH-001…006;
 * Data Model Bible §11; Technical Architecture Bible §13 §4; TDR-06/25).
 * [SearchIndexer]/[UnifiedSearchService] also work standalone; nothing here
 * depends on any `domain-*` module (out of this sprint's scope to wire up
 * — see sprint3_report.md).
 */
public class SearchService(
    driverFactory: DatabaseDriverFactory,
    public val recentSearches: RecentSearchStore = InMemoryRecentSearchStore(),
) {
    private val index = SqlDelightSearchIndex(driverFactory.createDriver())

    public val indexer: SearchIndexer = index
    public val search: UnifiedSearchService = index
}
