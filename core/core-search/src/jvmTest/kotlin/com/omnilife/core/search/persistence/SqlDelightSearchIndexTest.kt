package com.omnilife.core.search.persistence

import com.omnilife.core.search.SearchFilter
import com.omnilife.core.search.SimpleIndexableEntity
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test against the real SQLite FTS5 virtual table — the one
 * platform this sandbox can actually run (README-BUILD.md §4), same
 * pattern as `domain-task`'s `SqlDelightTaskRepositoryTest` since Sprint 1.
 */
class SqlDelightSearchIndexTest {
    private lateinit var index: SqlDelightSearchIndex

    private fun entity(
        id: String,
        title: String,
        content: String? = null,
        entityType: String = "task",
        category: String? = null,
        lifecycleState: String = "ACTIVE",
        modifiedAt: Instant = Instant.fromEpochSeconds(1000),
    ) = SimpleIndexableEntity(
        id = id,
        entityType = entityType,
        title = title,
        content = content,
        category = category,
        lifecycleState = lifecycleState,
        createdAt = Instant.fromEpochSeconds(0),
        modifiedAt = modifiedAt,
    )

    @BeforeTest
    fun setUp() {
        index = SqlDelightSearchIndex(DatabaseDriverFactory().createDriver())
    }

    @Test
    fun `an indexed entity is found by a title match`() {
        index.index(entity("task-1", title = "Chiamare il commercialista"))

        val results = index.search("commercialista")

        assertEquals(listOf("task-1"), results.map { it.id })
    }

    @Test
    fun `an indexed entity is found by a content match (SRCH-001 - titles, contents, notes)`() {
        index.index(entity("task-1", title = "Promemoria", content = "ask about invoice"))

        val results = index.search("invoice")

        assertEquals(listOf("task-1"), results.map { it.id })
    }

    @Test
    fun `SRCH-AC-02 - re-indexing after an edit makes the old term disappear and the new term appear immediately`() {
        index.index(entity("expense-1", title = "cena"))

        index.index(entity("expense-1", title = "pranzo"))

        assertTrue(index.search("pranzo").map { it.id }.contains("expense-1"))
        assertTrue(index.search("cena").isEmpty())
    }

    @Test
    fun `remove takes an entity out of the index`() {
        index.index(entity("task-1", title = "Chiamare il commercialista"))

        index.remove("task-1")

        assertEquals(emptyList(), index.search("commercialista"))
    }

    @Test
    fun `SRCH-002 - entityType filter narrows results to one module`() {
        index.index(entity("task-1", title = "dentista", entityType = "task"))
        index.index(entity("note-1", title = "dentista", entityType = "note"))

        val results = index.search("dentista", SearchFilter(entityType = "task"))

        assertEquals(listOf("task-1"), results.map { it.id })
    }

    @Test
    fun `SRCH-006 and INV-14 - archived entities are excluded by default and included only with the explicit filter`() {
        index.index(entity("task-1", title = "dentista", lifecycleState = "ARCHIVED"))

        assertEquals(emptyList(), index.search("dentista"))
        val withArchived = index.search("dentista", SearchFilter(includeArchivedOrTrashed = true))
        assertEquals(listOf("task-1"), withArchived.map { it.id })
    }

    @Test
    fun `ranking - a title match outranks a content-only match`() {
        index.index(entity("content-match", title = "unrelated", content = "dentista mentioned here"))
        index.index(entity("title-match", title = "dentista appointment"))

        val results = index.search("dentista")

        assertEquals("title-match", results.first().id)
    }

    @Test
    fun `ranking - among equal title-match status, the more recently modified entity ranks first`() {
        index.index(entity("older", title = "dentista", modifiedAt = Instant.fromEpochSeconds(100)))
        index.index(entity("newer", title = "dentista", modifiedAt = Instant.fromEpochSeconds(200)))

        val results = index.search("dentista")

        assertEquals(listOf("newer", "older"), results.map { it.id })
    }

    @Test
    fun `MFC-E-17 - special characters and emoji are treated literally, never as query syntax or a crash`() {
        index.index(entity("task-1", title = "call NEAR the office"))

        val results = index.search("NEAR")

        assertEquals(listOf("task-1"), results.map { it.id })
    }

    @Test
    fun `a 1-character query only matches a title prefix, never a general substring`() {
        index.index(entity("task-1", title = "dentista"))
        index.index(entity("task-2", title = "unrelated", content = "dentista mentioned in content only"))

        val results = index.search("d")

        assertEquals(listOf("task-1"), results.map { it.id })
    }

    @Test
    fun `rebuild replaces the entire index from scratch (a corrupted index is reconstructible)`() {
        index.index(entity("stale-1", title = "should be gone after rebuild"))

        index.rebuild(listOf(entity("fresh-1", title = "dentista")))

        assertEquals(1, index.count())
        assertEquals(listOf("fresh-1"), index.search("dentista").map { it.id })
    }
}
