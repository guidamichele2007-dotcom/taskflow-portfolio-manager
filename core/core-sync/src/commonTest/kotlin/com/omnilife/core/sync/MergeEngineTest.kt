package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MergeEngineTest {
    private val engine = MergeEngine()

    @Test
    fun `merging two snapshots of different entities throws`() {
        val local = MergeableEntitySnapshot("task-1", emptyMap(), emptyMap())
        val remote = MergeableEntitySnapshot("task-2", emptyMap(), emptyMap())

        assertFailsWith<IllegalArgumentException> { engine.merge(local, remote) }
    }

    @Test
    fun `merge resolves fields via LWW across both sides`() {
        val local =
            MergeableEntitySnapshot(
                "task-1",
                fields = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a"))),
                relations = emptyMap(),
            )
        val remote =
            MergeableEntitySnapshot(
                "task-1",
                fields = mapOf("title" to LwwRegister<Any?>("Buy bread", LogicalTimestamp(2, "device-b"))),
                relations = emptyMap(),
            )

        val merged = engine.merge(local, remote)

        assertEquals("Buy bread", merged.fields.getValue("title").value)
    }

    @Test
    fun `merge unions relation sets from both sides`() {
        val tagA = LogicalTimestamp(1, "device-a")
        val tagB = LogicalTimestamp(1, "device-b")
        val local =
            MergeableEntitySnapshot(
                "task-1",
                fields = emptyMap(),
                relations = mapOf("tags" to ORSet.empty<String>().add("urgent", tagA)),
            )
        val remote =
            MergeableEntitySnapshot(
                "task-1",
                fields = emptyMap(),
                relations = mapOf("tags" to ORSet.empty<String>().add("work", tagB)),
            )

        val merged = engine.merge(local, remote)

        assertEquals(setOf("urgent", "work"), merged.relations.getValue("tags").elements())
    }

    @Test
    fun `merge is commutative`() {
        val local =
            MergeableEntitySnapshot(
                "task-1",
                fields = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a"))),
                relations = mapOf("tags" to ORSet.empty<String>().add("urgent", LogicalTimestamp(1, "device-a"))),
            )
        val remote =
            MergeableEntitySnapshot(
                "task-1",
                fields = mapOf("title" to LwwRegister<Any?>("Buy bread", LogicalTimestamp(2, "device-b"))),
                relations = mapOf("tags" to ORSet.empty<String>().add("work", LogicalTimestamp(1, "device-b"))),
            )

        val mergedOneWay = engine.merge(local, remote)
        val mergedOtherWay = engine.merge(remote, local)

        val oneWayTags = mergedOneWay.relations.getValue("tags").elements()
        val otherWayTags = mergedOtherWay.relations.getValue("tags").elements()
        assertEquals(mergedOneWay.fields.getValue("title"), mergedOtherWay.fields.getValue("title"))
        assertEquals(oneWayTags, otherWayTags)
        assertTrue(oneWayTags == setOf("urgent", "work"))
    }

    @Test
    fun `a field only present on one side survives the merge untouched`() {
        val local =
            MergeableEntitySnapshot(
                "task-1",
                fields = mapOf("title" to LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a"))),
                relations = emptyMap(),
            )
        val remote = MergeableEntitySnapshot("task-1", fields = emptyMap(), relations = emptyMap())

        val merged = engine.merge(local, remote)

        assertEquals("Buy milk", merged.fields.getValue("title").value)
    }
}
