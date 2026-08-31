package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeltaGeneratorTest {
    @Test
    fun `identical baseline and current produce an empty delta`() {
        val fields = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a")))

        val delta = DeltaGenerator.generateDelta(fields, fields)

        assertTrue(delta.isEmpty())
        assertFalse(DeltaGenerator.hasChanges(fields, fields))
    }

    @Test
    fun `a field with a newer timestamp is included in the delta`() {
        val baseline = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a")))
        val current = mapOf("title" to LwwRegister<Any?>("Buy bread", LogicalTimestamp(2, "device-a")))

        val delta = DeltaGenerator.generateDelta(baseline, current)

        assertEquals(current, delta)
        assertTrue(DeltaGenerator.hasChanges(baseline, current))
    }

    @Test
    fun `a field with the same or older timestamp is excluded from the delta`() {
        val baseline = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(5, "device-a")))
        val staleCurrent = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(5, "device-a")))

        assertTrue(DeltaGenerator.generateDelta(baseline, staleCurrent).isEmpty())
    }

    @Test
    fun `a field absent from baseline always counts as changed`() {
        val baseline = emptyMap<String, LwwRegister<Any?>>()
        val current = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a")))

        val delta = DeltaGenerator.generateDelta(baseline, current)

        assertEquals(current, delta)
    }

    @Test
    fun `only changed fields are included, unchanged fields are omitted`() {
        val baseline =
            mapOf(
                "title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a")),
                "priority" to LwwRegister<Any?>("LOW", LogicalTimestamp(1, "device-a")),
            )
        val current =
            mapOf(
                "title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a")),
                "priority" to LwwRegister<Any?>("HIGH", LogicalTimestamp(2, "device-a")),
            )

        val delta = DeltaGenerator.generateDelta(baseline, current)

        assertEquals(setOf("priority"), delta.keys)
    }
}
