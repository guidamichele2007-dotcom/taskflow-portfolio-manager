package com.omnilife.core.sync.persistence

import app.cash.sqldelight.db.SqlDriver
import com.omnilife.core.sync.LogicalTimestamp
import com.omnilife.core.sync.OutboxItem
import com.omnilife.core.sync.SyncOutboxStore

/**
 * SQLite-backed [SyncOutboxStore] (MFC §3: "outbox persistente, sopravvive
 * al kill") — the crash-resilient replacement for
 * [com.omnilife.core.sync.InMemorySyncOutboxStore], which loses its queue
 * the moment the process dies. Every mutating call commits synchronously;
 * there is no write-behind buffer to lose on a crash.
 */
public class SqlDelightSyncOutboxStore(driver: SqlDriver) : SyncOutboxStore {
    private val database = SyncDatabase(driver)
    private val queries = database.syncOutboxQueries

    override fun enqueue(item: OutboxItem) {
        queries.insertOrReplaceItem(
            id = item.id,
            payload = item.payload,
            enqueuedAtCounter = item.enqueuedAt.counter,
            enqueuedAtDeviceId = item.enqueuedAt.deviceId,
            isHot = if (item.isHot) 1L else 0L,
        )
    }

    override fun peekNext(): OutboxItem? =
        queries
            .selectAll()
            .executeAsList()
            .map(::toOutboxItem)
            .minWithOrNull(compareByDescending<OutboxItem> { it.isHot }.thenBy { it.enqueuedAt })

    override fun acknowledge(id: String) {
        queries.deleteItem(id)
    }

    override fun size(): Int = queries.countAll().executeAsOne().toInt()

    override fun peekAll(): List<OutboxItem> = queries.selectAll().executeAsList().map(::toOutboxItem)

    private fun toOutboxItem(row: OutboxRow): OutboxItem =
        OutboxItem(
            id = row.id,
            payload = row.payload,
            enqueuedAt = LogicalTimestamp(row.enqueuedAtCounter, row.enqueuedAtDeviceId),
            isHot = row.isHot != 0L,
        )
}
