package com.omnilife.core.search

import com.omnilife.core.search.persistence.DatabaseDriverFactory
import com.omnilife.core.search.persistence.SqlDelightSearchIndex
import kotlinx.datetime.Instant
import kotlin.system.measureNanoTime
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Real benchmark against the Bible's own numeric target — MFC-AC-07:
 * "primi risultati ≤ 100 ms su 50.000 entità" — not a synthetic number,
 * the actual acceptance criterion. Hand-rolled (see sprint3_report.md for
 * why not JMH), run as a normal JVM test.
 */
class SearchBenchmark {
    @Test
    fun `benchmark - MFC-AC-07 - search stays under 100ms at 50,000 indexed entities`() {
        val index = SqlDelightSearchIndex(DatabaseDriverFactory().createDriver())
        val entityCount = 50_000

        val entities =
            (0 until entityCount).map { i ->
                SimpleIndexableEntity(
                    id = "entity-$i",
                    entityType = if (i % 3 == 0) "task" else "note",
                    title = if (i == entityCount / 2) "Chiamare il commercialista" else "Untitled item $i",
                    content = "some filler content for item number $i",
                    category = null,
                    createdAt = Instant.fromEpochSeconds(0),
                    modifiedAt = Instant.fromEpochSeconds(i.toLong()),
                )
            }

        val indexNanos = measureNanoTime { index.rebuild(entities) }
        println("[benchmark] indexed $entityCount entities in ${indexNanos / 1_000_000}ms")

        val searchNanos = measureNanoTime { index.search("commercialista") }
        val searchMs = searchNanos / 1_000_000
        println("[benchmark] MFC-AC-07 search at $entityCount entities: ${searchMs}ms (target: <=100ms)")

        assertTrue(searchMs <= 100, "search took ${searchMs}ms, exceeding the MFC-AC-07 100ms budget")
    }
}
