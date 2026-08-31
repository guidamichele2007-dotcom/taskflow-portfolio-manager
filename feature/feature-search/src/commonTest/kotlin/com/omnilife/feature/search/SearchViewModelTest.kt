package com.omnilife.feature.search

import com.omnilife.core.search.InMemoryRecentSearchStore
import com.omnilife.core.search.SearchFilter
import com.omnilife.core.search.SearchResult
import com.omnilife.core.search.UnifiedSearchService
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeSearchService(private val resultsByQuery: Map<String, List<SearchResult>> = emptyMap()) :
    UnifiedSearchService {
    var lastFilter: SearchFilter? = null
        private set

    override fun search(
        query: String,
        filter: SearchFilter,
    ): List<SearchResult> {
        lastFilter = filter
        return resultsByQuery[query].orEmpty()
    }
}

private fun result(id: String) =
    SearchResult(id, "task", "Titolo $id", null, "ACTIVE", Instant.parse("2026-01-01T09:00:00Z"))

class SearchViewModelTest {
    @Test
    fun `a blank query yields no results and does not call the search service`() {
        val viewModel = SearchViewModel(FakeSearchService(), InMemoryRecentSearchStore())

        viewModel.dispatch(SearchIntent.ChangeQuery(""))

        assertTrue(viewModel.state.value.results.isEmpty())
    }

    @Test
    fun `a non-blank query delegates to UnifiedSearchService and stores real results`() {
        val service = FakeSearchService(mapOf("latte" to listOf(result("t1"))))
        val viewModel = SearchViewModel(service, InMemoryRecentSearchStore())

        viewModel.dispatch(SearchIntent.ChangeQuery("latte"))

        assertEquals(listOf(result("t1")), viewModel.state.value.results)
        assertEquals("latte", viewModel.state.value.query)
    }

    @Test
    fun `a query matching nothing is a genuine empty-result state, not a fake result`() {
        val viewModel = SearchViewModel(FakeSearchService(), InMemoryRecentSearchStore())

        viewModel.dispatch(SearchIntent.ChangeQuery("nothing matches this"))

        assertTrue(viewModel.state.value.isEmptyResult)
    }

    @Test
    fun `ResultOpened records the query into recent searches, typing alone does not`() {
        val recentSearchStore = InMemoryRecentSearchStore()
        val service = FakeSearchService(mapOf("latte" to listOf(result("t1"))))
        val viewModel = SearchViewModel(service, recentSearchStore)
        viewModel.dispatch(SearchIntent.ChangeQuery("latte"))
        assertTrue(viewModel.state.value.recentSearches.isEmpty())

        viewModel.dispatch(SearchIntent.ResultOpened)

        assertEquals(listOf("latte"), viewModel.state.value.recentSearches)
    }

    @Test
    fun `SelectRecentSearch re-runs that query`() {
        val service = FakeSearchService(mapOf("latte" to listOf(result("t1"))))
        val viewModel = SearchViewModel(service, InMemoryRecentSearchStore())

        viewModel.dispatch(SearchIntent.SelectRecentSearch("latte"))

        assertEquals(listOf(result("t1")), viewModel.state.value.results)
    }

    @Test
    fun `RemoveRecentSearch removes exactly that entry`() {
        val recentSearchStore = InMemoryRecentSearchStore().apply { record("latte"); record("pane") }
        val viewModel = SearchViewModel(FakeSearchService(), recentSearchStore)

        viewModel.dispatch(SearchIntent.RemoveRecentSearch("latte"))

        assertEquals(listOf("pane"), viewModel.state.value.recentSearches)
    }

    @Test
    fun `ClearRecentSearches empties the whole list`() {
        val recentSearchStore = InMemoryRecentSearchStore().apply { record("latte"); record("pane") }
        val viewModel = SearchViewModel(FakeSearchService(), recentSearchStore)

        viewModel.dispatch(SearchIntent.ClearRecentSearches)

        assertTrue(viewModel.state.value.recentSearches.isEmpty())
    }

    @Test
    fun `ChangeFilter re-runs the current query with the new filter`() {
        val service = FakeSearchService(mapOf("latte" to listOf(result("t1"))))
        val viewModel = SearchViewModel(service, InMemoryRecentSearchStore())
        viewModel.dispatch(SearchIntent.ChangeQuery("latte"))

        viewModel.dispatch(SearchIntent.ChangeFilter(SearchFilter(entityType = "task")))

        assertEquals("task", service.lastFilter?.entityType)
        assertEquals(SearchFilter(entityType = "task"), viewModel.state.value.filter)
    }
}
