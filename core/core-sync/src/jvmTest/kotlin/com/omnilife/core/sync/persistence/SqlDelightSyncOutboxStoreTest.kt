package com.omnilife.core.sync.persistence

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.omnilife.core.sync.LogicalTimestamp
import com.omnilife.core.sync.OutboxItem
import kotlin.io.path.createTempFile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Verifies [SqlDelightSyncOutboxStore] against a real file-backed SQLite
 * database (not [JdbcSqliteDriver.IN_MEMORY]) so the crash-resilience test
 * below genuinely proves persistence across a driver recreation — an
 * in-memory driver would trivially "survive" only because nothing ever
 * left process memory in the first place.
 */
class SqlDelightSyncOutboxStoreTest {
    private lateinit var dbFile: java.io.File

    @BeforeTest
    fun setUp() {
        dbFile = createTempFile(prefix = "omnilife-sync-outbox-test", suffix = ".db").toFile()
        dbFile.delete()
    }

    @AfterTest
    fun tearDown() {
        dbFile.delete()
    }

    private fun openStore(): SqlDelightSyncOutboxStore {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        SyncDatabase.Schema.create(driver)
        return SqlDelightSyncOutboxStore(driver)
    }

    /**
     * SyncDatabase.Schema.create() re-running CREATE TABLE against an
     * already-populated file would fail; a real driver factory only calls
     * it once per file. This test reopens against the same file without
     * re-creating the schema, exactly like a real app restart would.
     */
    private fun reopenStoreWithoutSchemaCreation(): SqlDelightSyncOutboxStore {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        return SqlDelightSyncOutboxStore(driver)
    }

    private fun item(
        id: String,
        isHot: Boolean = false,
    ) = OutboxItem(id, "payload-$id".encodeToByteArray(), LogicalTimestamp(1, "device-a"), isHot)

    @Test
    fun `enqueue then peekNext returns the item`() {
        val store = openStore()
        store.enqueue(item("a"))

        assertEquals("a", store.peekNext()?.id)
    }

    @Test
    fun `acknowledge removes the item`() {
        val store = openStore()
        store.enqueue(item("a"))

        store.acknowledge("a")

        assertNull(store.peekNext())
        assertEquals(0, store.size())
    }

    @Test
    fun `acknowledging an id that was never enqueued is idempotent, not an error`() {
        val store = openStore()
        store.acknowledge("never-enqueued")
        assertEquals(0, store.size())
    }

    @Test
    fun `acknowledging the same id twice is idempotent`() {
        val store = openStore()
        store.enqueue(item("a"))

        store.acknowledge("a")
        store.acknowledge("a")

        assertEquals(0, store.size())
    }

    @Test
    fun `hot items are peeked before non-hot items regardless of enqueue order`() {
        val store = openStore()
        store.enqueue(item("cold", isHot = false))
        store.enqueue(item("hot", isHot = true))

        assertEquals("hot", store.peekNext()?.id)
    }

    @Test
    fun `re-enqueuing the same id replaces the item rather than duplicating it`() {
        val store = openStore()
        store.enqueue(item("a", isHot = false))
        store.enqueue(item("a", isHot = true))

        assertEquals(1, store.size())
        assertEquals(true, store.peekAll().single().isHot)
    }

    @Test
    fun `queued items survive the store being recreated against the same file - crash resilience`() {
        val firstProcess = openStore()
        firstProcess.enqueue(item("survives-a"))
        firstProcess.enqueue(item("survives-b", isHot = true))

        // Simulates the process dying and restarting: a brand-new driver/store instance,
        // no in-memory state carried over, reopened against the same on-disk file.
        val afterRestart = reopenStoreWithoutSchemaCreation()

        assertEquals(2, afterRestart.size())
        assertEquals(setOf("survives-a", "survives-b"), afterRestart.peekAll().map { it.id }.toSet())
        assertEquals("survives-b", afterRestart.peekNext()?.id)
    }

    @Test
    fun `an acknowledged item does not reappear after a restart`() {
        val firstProcess = openStore()
        firstProcess.enqueue(item("a"))
        firstProcess.enqueue(item("b"))
        firstProcess.acknowledge("a")

        val afterRestart = reopenStoreWithoutSchemaCreation()

        assertEquals(listOf("b"), afterRestart.peekAll().map { it.id })
    }

    @Test
    fun `payload bytes survive a restart unchanged`() {
        val firstProcess = openStore()
        firstProcess.enqueue(item("a"))

        val afterRestart = reopenStoreWithoutSchemaCreation()

        assertEquals("payload-a", afterRestart.peekAll().single().payload.decodeToString())
    }
}
