package com.omnilife.core.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SearchSuggestionsTest {
    @Test
    fun `finds the closest known term within edit distance 2 (light fuzzy tolerance)`() {
        val suggestion = SearchSuggestions.didYouMean("dentst", listOf("dentista", "commercialista", "avvocato"))

        assertEquals("dentista", suggestion)
    }

    @Test
    fun `returns null when nothing is close enough (never a surprising silent match)`() {
        val suggestion = SearchSuggestions.didYouMean("dentst", listOf("commercialista", "avvocato"))

        assertNull(suggestion)
    }

    @Test
    fun `an exact match is not a suggestion (there is nothing to suggest instead)`() {
        val suggestion = SearchSuggestions.didYouMean("dentista", listOf("dentista"))

        assertNull(suggestion)
    }

    @Test
    fun `levenshteinDistance matches known reference values`() {
        assertEquals(0, SearchSuggestions.levenshteinDistance("kitten", "kitten"))
        assertEquals(3, SearchSuggestions.levenshteinDistance("kitten", "sitting"))
        assertEquals(1, SearchSuggestions.levenshteinDistance("dentista", "dentist"))
    }
}
