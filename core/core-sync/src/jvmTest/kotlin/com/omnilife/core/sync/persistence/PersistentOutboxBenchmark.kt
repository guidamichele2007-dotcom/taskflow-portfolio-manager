package com.omnilife.core.sync.persistence

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.omnilife.core.sync.LogicalTimestamp
import com.omnilife.core.sync.OutboxItem
import kotlin.system.measureNanoTime
import kotlin.test.Test

/**
 * Hand-rolled micro-benchmark (see sprint3_report.md for why not JMH) for
 * the persistent, SQLite-backed Local Change Queue — the in-memory
 * [com.omnilife.core.sync.InMemorySyncOutboxStore] has no I/O cost to
 * measure, but every real device write goes through this class, so its
 * throughput is what actually bounds "how fast can the app enqueue local
 * changes."
 */
class PersistentOutboxBenchmark {
    @Test
    fun `benchmark - enqueue throughput for 10,000 outbox items`() {
        val dbFile = kotlin.io.path.createTempFile(prefix = "omnilife-sync-outbox-bench", suffix = ".db").toFile()
        dbFile.delete()
        try {
            val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
            SyncDatabase.Schema.create(driver)
            val store = SqlDelightSyncOutboxStore(driver)
            val itemCount = 10_000

            val elapsedNanos =
                measureNanoTime {
                    repeat(itemCount) { i ->
                        store.enqueue(
                            OutboxItem(
                                id = "item-$i",
                                payload = ByteArray(128),
                                enqueuedAt = LogicalTimestamp(i.toLong(), "device-a"),
                                isHot = i % 10 == 0,
                            ),
                        )
                    }
                }

            val elapsedMs = elapsedNanos / 1_000_000
            println(
                "[benchmark] SqlDelightSyncOutboxStore.enqueue: $itemCount items in ${elapsedMs}ms " +
                    "(${"%.2f".format(itemCount / (elapsedNanos / 1_000_000_000.0))} enqueues/s)",
            )
        } finally {
            dbFile.delete()
        }
    }

    @Test
    fun `benchmark - peekNext latency with 10,000 items queued`() {
        val dbFile = kotlin.io.path.createTempFile(prefix = "omnilife-sync-outbox-bench", suffix = ".db").toFile()
        dbFile.delete()
        try {
            val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
            SyncDatabase.Schema.create(driver)
            val store = SqlDelightSyncOutboxStore(driver)
            repeat(10_000) { i ->
                store.enqueue(
                    OutboxItem("item-$i", ByteArray(128), LogicalTimestamp(i.toLong(), "device-a"), i % 10 == 0),
                )
            }

            val elapsedNanos = measureNanoTime { store.peekNext() }
            val elapsedMs = elapsedNanos / 1_000_000

            println("[benchmark] SqlDelightSyncOutboxStore.peekNext: ${elapsedMs}ms with 10,000 rows queued")
        } finally {
            dbFile.delete()
        }
    }
}
