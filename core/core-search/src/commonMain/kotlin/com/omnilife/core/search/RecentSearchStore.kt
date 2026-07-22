package com.omnilife.core.search

/**
 * SRCH-004/SRCH-R-01: the last 10 searches, individually/bulk-clearable,
 * **local-only** — "la ricerca non registra né sincronizza le query"
 * (C-art. 45, minimization: search history is one of the most
 * intention-revealing kinds of data a person has). This module never wires
 * this into [com.omnilife.core.sync]'s outbox — that would be a bug, not a
 * missing feature.
 */
public interface RecentSearchStore {
    public fun record(query: String)

    public fun recent(): List<String>

    public fun clear(query: String)

    public fun clearAll()
}

public class InMemoryRecentSearchStore : RecentSearchStore {
    private val queries = ArrayDeque<String>()

    override fun record(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        queries.remove(trimmed)
        queries.addFirst(trimmed)
        while (queries.size > MAX_RECENT) {
            queries.removeLast()
        }
    }

    override fun recent(): List<String> = queries.toList()

    override fun clear(query: String) {
        queries.remove(query)
    }

    override fun clearAll() {
        queries.clear()
    }

    private companion object {
        const val MAX_RECENT = 10
    }
}
