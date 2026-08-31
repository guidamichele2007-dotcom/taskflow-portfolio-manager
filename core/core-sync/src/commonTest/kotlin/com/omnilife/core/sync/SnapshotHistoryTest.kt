package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** NOTE-006/NOTE-AC-03: the losing snapshot is kept in history, never dropped (INV-07). */
class SnapshotHistoryTest {
    @Test
    fun `the later snapshot wins and the earlier one is kept in history`() {
        val a = SnapshotHistory.initial("draft one", LogicalTimestamp(1, "device-a"))
        val b = SnapshotHistory.initial("draft two", LogicalTimestamp(2, "device-b"))

        val merged = SnapshotHistory.merge(a, b)

        assertEquals("draft two", merged.current.value)
        assertTrue(merged.history.any { it.value == "draft one" })
    }

    @Test
    fun `merge is commutative`() {
        val a = SnapshotHistory.initial("draft one", LogicalTimestamp(1, "device-a"))
        val b = SnapshotHistory.initial("draft two", LogicalTimestamp(2, "device-b"))

        assertEquals(SnapshotHistory.merge(a, b).current, SnapshotHistory.merge(b, a).current)
    }

    @Test
    fun `no version is ever lost across repeated merges`() {
        val v1 = SnapshotHistory.initial("v1", LogicalTimestamp(1, "device-a"))
        val v2 = SnapshotHistory.merge(v1, SnapshotHistory.initial("v2", LogicalTimestamp(2, "device-a")))
        val v3 = SnapshotHistory.merge(v2, SnapshotHistory.initial("v3", LogicalTimestamp(3, "device-a")))

        val allValues = (v3.history.map { it.value } + v3.current.value).toSet()
        assertEquals(setOf("v1", "v2", "v3"), allValues)
    }
}
