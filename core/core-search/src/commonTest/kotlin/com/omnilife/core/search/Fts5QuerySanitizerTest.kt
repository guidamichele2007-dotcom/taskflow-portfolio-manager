package com.omnilife.core.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** MFC-E-17: special characters/emoji must be literal, never hidden query syntax. */
class Fts5QuerySanitizerTest {
    @Test
    fun `every token is wrapped as a literal quoted phrase`() {
        val result = Fts5QuerySanitizer.buildMatchQuery("dentista appuntamento")

        assertEquals("\"dentista\" \"appuntamento\"", result)
    }

    @Test
    fun `FTS5 special characters are neutralized, never interpreted as query syntax`() {
        val result = Fts5QuerySanitizer.sanitizeToken("NEAR")

        assertEquals("\"NEAR\"", result)
    }

    @Test
    fun `a literal double quote in the query is escaped, not left to break the FTS5 expression`() {
        val result = Fts5QuerySanitizer.sanitizeToken("say\"hi")

        assertEquals("\"say\"\"hi\"", result)
    }

    @Test
    fun `a single character query builds a title-only prefix expression`() {
        val result = Fts5QuerySanitizer.buildTitlePrefixQuery("d")

        assertTrue(result.startsWith("title: "))
        assertTrue(result.endsWith("*"))
    }

    @Test
    fun `emoji and unicode are passed through as literal content, not stripped`() {
        val result = Fts5QuerySanitizer.buildMatchQuery("🎉 party")

        assertEquals("\"🎉\" \"party\"", result)
    }
}
