package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VersionManagerTest {
    @Test
    fun `currentVersion is null before anything is recorded`() {
        val manager = InMemoryVersionManager()
        assertNull(manager.currentVersion("task-1"))
    }

    @Test
    fun `recordVersion stores the version`() {
        val manager = InMemoryVersionManager()
        val version = LogicalTimestamp(3, "device-a")

        manager.recordVersion("task-1", version)

        assertEquals(version, manager.currentVersion("task-1"))
    }

    @Test
    fun `recordVersion ignores an older version than what is recorded`() {
        val manager = InMemoryVersionManager()
        manager.recordVersion("task-1", LogicalTimestamp(5, "device-a"))

        manager.recordVersion("task-1", LogicalTimestamp(2, "device-a"))

        assertEquals(LogicalTimestamp(5, "device-a"), manager.currentVersion("task-1"))
    }

    @Test
    fun `recordVersion is idempotent for the same version applied twice`() {
        val manager = InMemoryVersionManager()
        val version = LogicalTimestamp(5, "device-a")

        manager.recordVersion("task-1", version)
        manager.recordVersion("task-1", version)

        assertEquals(version, manager.currentVersion("task-1"))
    }

    @Test
    fun `isNewerThanRecorded is true for any candidate when nothing is recorded yet`() {
        val manager = InMemoryVersionManager()
        assertTrue(manager.isNewerThanRecorded("task-1", LogicalTimestamp(0, "device-a")))
    }

    @Test
    fun `isNewerThanRecorded compares against the recorded version`() {
        val manager = InMemoryVersionManager()
        manager.recordVersion("task-1", LogicalTimestamp(5, "device-a"))

        assertTrue(manager.isNewerThanRecorded("task-1", LogicalTimestamp(6, "device-a")))
        assertFalse(manager.isNewerThanRecorded("task-1", LogicalTimestamp(5, "device-a")))
        assertFalse(manager.isNewerThanRecorded("task-1", LogicalTimestamp(4, "device-a")))
    }

    @Test
    fun `versions are tracked independently per entity`() {
        val manager = InMemoryVersionManager()
        manager.recordVersion("task-1", LogicalTimestamp(5, "device-a"))
        manager.recordVersion("task-2", LogicalTimestamp(1, "device-a"))

        assertEquals(LogicalTimestamp(5, "device-a"), manager.currentVersion("task-1"))
        assertEquals(LogicalTimestamp(1, "device-a"), manager.currentVersion("task-2"))
    }
}
