package com.omnilife.core.search.persistence

import app.cash.sqldelight.db.SqlDriver
import com.omnilife.core.search.Fts5QuerySanitizer
import com.omnilife.core.search.IndexableEntity
import com.omnilife.core.search.SearchFilter
import com.omnilife.core.search.SearchIndexer
import com.omnilife.core.search.SearchResult
import com.omnilife.core.search.UnifiedSearchService
import kotlinx.datetime.Instant

/**
 * SQLite FTS5-backed global index (TDR-25) — both [SearchIndexer] (writes)
 * and [UnifiedSearchService] (reads) share the same underlying table
 * because they're two views of one derived projection, not two data
 * stores.
 */
public class SqlDelightSearchIndex(driver: SqlDriver) : SearchIndexer, UnifiedSearchService {
    private val database = SearchDatabase(driver)
    private val queries = database.searchIndexQueries

    override fun index(entity: IndexableEntity) {
        database.transaction {
            queries.removeEntry(entity.id)
            queries.insertEntry(
                entityId = entity.id,
                entityType = entity.entityType,
                title = entity.title,
                content = entity.content,
                category = entity.category,
                lifecycleState = entity.lifecycleState,
                createdAtEpochSeconds = entity.createdAt.epochSeconds,
                modifiedAtEpochSeconds = entity.modifiedAt.epochSeconds,
            )
        }
    }

    override fun remove(entityId: String) {
        queries.removeEntry(entityId)
    }

    override fun rebuild(entities: List<IndexableEntity>) {
        database.transaction {
            queries.deleteAll()
            entities.forEach { entity ->
                queries.insertEntry(
                    entityId = entity.id,
                    entityType = entity.entityType,
                    title = entity.title,
                    content = entity.content,
                    category = entity.category,
                    lifecycleState = entity.lifecycleState,
                    createdAtEpochSeconds = entity.createdAt.epochSeconds,
                    modifiedAtEpochSeconds = entity.modifiedAt.epochSeconds,
                )
            }
        }
    }

    override fun count(): Long = queries.countAll().executeAsOne()

    override fun search(
        query: String,
        filter: SearchFilter,
    ): List<SearchResult> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        val ftsQuery =
            if (trimmed.length == 1) {
                Fts5QuerySanitizer.buildTitlePrefixQuery(trimmed)
            } else {
                Fts5QuerySanitizer.buildMatchQuery(trimmed)
            }

        val rows = queries.matchQuery(ftsQuery).executeAsList()
        return rows
            .asSequence()
            .filter { filter.entityType == null || it.entityType == filter.entityType }
            .filter { filter.category == null || it.category == filter.category }
            .filter { filter.includeArchivedOrTrashed || it.lifecycleState == "ACTIVE" }
            .map { row ->
                // FTS5 columns are nullable to SQLite regardless of typename (no NOT
                // NULL support on virtual tables) — these are never actually null,
                // insertEntry always binds every column.
                SearchResult(
                    id = requireNotNull(row.entityId),
                    entityType = requireNotNull(row.entityType),
                    title = row.title.orEmpty(),
                    category = row.category,
                    lifecycleState = requireNotNull(row.lifecycleState),
                    modifiedAt = Instant.fromEpochSeconds(requireNotNull(row.modifiedAtEpochSeconds)),
                )
            }.sortedWith(rankingComparator(trimmed))
            .toList()
    }

    /** SRCH-001: title match > content match, recent > old, active > archived — never an opaque score (TDR-25). */
    private fun rankingComparator(query: String): Comparator<SearchResult> =
        compareByDescending<SearchResult> { it.title.contains(query, ignoreCase = true) }
            .thenByDescending { it.modifiedAt }
            .thenByDescending { it.lifecycleState == "ACTIVE" }
}
