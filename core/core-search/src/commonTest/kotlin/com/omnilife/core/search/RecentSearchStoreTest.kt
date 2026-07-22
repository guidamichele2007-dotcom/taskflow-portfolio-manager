package com.omnilife.core.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** SRCH-004: last 10, individually/bulk-clearable. */
class RecentSearchStoreTest {
    @Test
    fun `records searches with the most recent first`() {
        val store = InMemoryRecentSearchStore()

        store.record("dentista")
        store.record("commercialista")

        assertEquals(listOf("commercialista", "dentista"), store.recent())
    }

    @Test
    fun `re-recording an existing query moves it to the front instead of duplicating`() {
        val store = InMemoryRecentSearchStore()
        store.record("dentista")
        store.record("commercialista")

        store.record("dentista")

        assertEquals(listOf("dentista", "commercialista"), store.recent())
    }

    @Test
    fun `SRCH-004 - keeps at most 10 entries, dropping the oldest`() {
        val store = InMemoryRecentSearchStore()
        (1..12).forEach { store.record("query-$it") }

        val recent = store.recent()

        assertEquals(10, recent.size)
        assertEquals("query-12", recent.first())
        assertTrue("query-1" !in recent && "query-2" !in recent)
    }

    @Test
    fun `clear removes a single query, clearAll removes everything`() {
        val store = InMemoryRecentSearchStore()
        store.record("dentista")
        store.record("commercialista")

        store.clear("dentista")
        assertEquals(listOf("commercialista"), store.recent())

        store.clearAll()
        assertEquals(emptyList(), store.recent())
    }

    @Test
    fun `blank queries are not recorded`() {
        val store = InMemoryRecentSearchStore()

        store.record("   ")

        assertEquals(emptyList(), store.recent())
    }
}
