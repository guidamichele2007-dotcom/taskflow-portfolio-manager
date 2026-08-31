package com.omnilife.core.search

/**
 * Turns a raw user query into a safe SQLite FTS5 `MATCH` expression
 * (TDR-25, MFC-E-17: "caratteri speciali/emoji → letterali, mai sintassi
 * nascosta che sorprende... mai injection nei campi di ricerca/filtri").
 * Every token is wrapped in double quotes — FTS5 treats a quoted string as
 * a literal phrase, so `" * ^ NEAR AND OR NOT -` inside a token can never
 * be interpreted as query syntax; a literal `"` is escaped by doubling it,
 * FTS5's own escaping convention for quoted strings.
 *
 * SRCH-001's 1-character edge case ("query di 1 carattere → solo prefissi
 * sui titoli") is handled by [buildTitlePrefixQuery] instead — a single
 * character is too broad for a general full-text match.
 */
public object Fts5QuerySanitizer {
    public fun sanitizeToken(token: String): String = "\"" + token.replace("\"", "\"\"") + "\""

    /** A literal, non-prefix, non-syntax phrase match across every indexed column. */
    public fun buildMatchQuery(rawQuery: String): String =
        rawQuery
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .joinToString(" ") { sanitizeToken(it) }

    /** SRCH-001: a 1-character query restricted to a title prefix — `title: "x"*`. */
    public fun buildTitlePrefixQuery(rawQuery: String): String = "title: ${sanitizeToken(rawQuery.trim())}*"
}
