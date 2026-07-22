package com.omnilife.core.search

/**
 * SRCH-001's declared "tolleranza ai refusi (fuzzy leggero) dichiarata nei
 * risultati ('stavi cercando…')" — a light, disclosed typo-tolerance
 * suggestion, never a silent reranking of results. Callers show this only
 * when [UnifiedSearchService.search] returns zero results.
 */
public object SearchSuggestions {
    private const val MAX_EDIT_DISTANCE = 2

    /** The closest term in [knownTerms] to [query], or `null` if nothing is within [MAX_EDIT_DISTANCE] edits. */
    public fun didYouMean(
        query: String,
        knownTerms: Collection<String>,
    ): String? {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isEmpty()) return null
        return knownTerms
            .distinct()
            .map { term -> term to levenshteinDistance(normalizedQuery, term.lowercase()) }
            .filter { (_, distance) -> distance in 1..MAX_EDIT_DISTANCE }
            .minByOrNull { (_, distance) -> distance }
            ?.first
    }

    internal fun levenshteinDistance(
        a: String,
        b: String,
    ): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        var previousRow = IntArray(b.length + 1) { it }
        for (i in 1..a.length) {
            val currentRow = IntArray(b.length + 1)
            currentRow[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                currentRow[j] =
                    minOf(
                        currentRow[j - 1] + 1,
                        previousRow[j] + 1,
                        previousRow[j - 1] + cost,
                    )
            }
            previousRow = currentRow
        }
        return previousRow[b.length]
    }
}
