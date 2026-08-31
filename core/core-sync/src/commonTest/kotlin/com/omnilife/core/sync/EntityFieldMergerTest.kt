package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals

/** MFC-R-08: "modifiche concorrenti da più dispositivi convergono automaticamente per-campo". */
class EntityFieldMergerTest {
    @Test
    fun `disjoint field edits from two devices both survive`() {
        val deviceA = mapOf("title" to LwwRegister("New title", LogicalTimestamp(2, "device-a")))
        val deviceB = mapOf("priority" to LwwRegister("HIGH", LogicalTimestamp(1, "device-b")))

        val merged = EntityFieldMerger.merge(deviceA, deviceB)

        assertEquals("New title", merged.getValue("title").value)
        assertEquals("HIGH", merged.getValue("priority").value)
    }

    @Test
    fun `the same field edited on both devices resolves by the later logical timestamp`() {
        val deviceA = mapOf("title" to LwwRegister("Title from A", LogicalTimestamp(5, "device-a")))
        val deviceB = mapOf("title" to LwwRegister("Title from B", LogicalTimestamp(7, "device-b")))

        val merged = EntityFieldMerger.merge(deviceA, deviceB)

        assertEquals("Title from B", merged.getValue("title").value)
    }

    @Test
    fun `merge is order-independent (commutative) at the whole-entity level`() {
        val deviceA =
            mapOf(
                "title" to LwwRegister("A wins here", LogicalTimestamp(9, "device-a")),
                "notes" to LwwRegister("older notes", LogicalTimestamp(1, "device-a")),
            )
        val deviceB =
            mapOf(
                "title" to LwwRegister("stale title", LogicalTimestamp(2, "device-b")),
                "notes" to LwwRegister("newer notes", LogicalTimestamp(4, "device-b")),
            )

        assertEquals(EntityFieldMerger.merge(deviceA, deviceB), EntityFieldMerger.merge(deviceB, deviceA))
    }
}
