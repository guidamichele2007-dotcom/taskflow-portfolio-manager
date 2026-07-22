package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChangeTrackerTest {
    @Test
    fun `an entity is not dirty until marked`() {
        val tracker = InMemoryChangeTracker()
        assertFalse(tracker.isDirty("task-1"))
    }

    @Test
    fun `markDirty makes an entity dirty`() {
        val tracker = InMemoryChangeTracker()
        tracker.markDirty("task-1", LogicalTimestamp(1, "device-a"))
        assertTrue(tracker.isDirty("task-1"))
    }

    @Test
    fun `marking dirty twice collapses to one dirty entry, not a counter`() {
        val tracker = InMemoryChangeTracker()
        tracker.markDirty("task-1", LogicalTimestamp(1, "device-a"))
        tracker.markDirty("task-1", LogicalTimestamp(2, "device-a"))

        assertEquals(setOf("task-1"), tracker.dirtyEntityIds())
    }

    @Test
    fun `clear removes exactly one entity from the dirty set`() {
        val tracker = InMemoryChangeTracker()
        tracker.markDirty("task-1", LogicalTimestamp(1, "device-a"))
        tracker.markDirty("task-2", LogicalTimestamp(1, "device-a"))

        tracker.clear("task-1")

        assertFalse(tracker.isDirty("task-1"))
        assertTrue(tracker.isDirty("task-2"))
    }

    @Test
    fun `clearAll empties the dirty set`() {
        val tracker = InMemoryChangeTracker()
        tracker.markDirty("task-1", LogicalTimestamp(1, "device-a"))
        tracker.markDirty("task-2", LogicalTimestamp(1, "device-a"))

        tracker.clearAll()

        assertEquals(emptySet(), tracker.dirtyEntityIds())
    }

    @Test
    fun `clearing an entity that was never dirty is a no-op, not an error`() {
        val tracker = InMemoryChangeTracker()
        tracker.clear("never-existed")
        assertEquals(emptySet(), tracker.dirtyEntityIds())
    }
}
