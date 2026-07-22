package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** INV-04: "i GraphLink offline-creati convergono per unione insiemistica (mai persi)". */
class ORSetConvergenceTest {
    @Test
    fun `concurrent adds from two devices both survive the merge`() {
        val deviceA = ORSet.empty<String>().add("link-1", LogicalTimestamp(1, "device-a"))
        val deviceB = ORSet.empty<String>().add("link-2", LogicalTimestamp(1, "device-b"))

        val merged = deviceA.merge(deviceB)

        assertEquals(setOf("link-1", "link-2"), merged.elements())
    }

    @Test
    fun `a concurrent remove on one device removes the element after merge, regardless of arrival order`() {
        val base = ORSet.empty<String>().add("link-1", LogicalTimestamp(1, "device-a"))
        val deviceARemoves = base.remove("link-1")
        val deviceBUnchanged = base

        val mergedAThenB = deviceARemoves.merge(deviceBUnchanged)
        val mergedBThenA = deviceBUnchanged.merge(deviceARemoves)

        assertTrue("link-1" !in mergedAThenB.elements())
        assertTrue("link-1" !in mergedBThenA.elements())
    }

    @Test
    fun `re-adding a removed element makes it present again (observed-remove semantics)`() {
        val tag1 = LogicalTimestamp(1, "device-a")
        val tag2 = LogicalTimestamp(2, "device-a")
        val added = ORSet.empty<String>().add("link-1", tag1)
        val removed = added.remove("link-1")
        val reAdded = removed.add("link-1", tag2)

        assertTrue(reAdded.contains("link-1"))
    }

    @Test
    fun `merge is commutative`() {
        val a = ORSet.empty<String>().add("x", LogicalTimestamp(1, "device-a"))
        val b = ORSet.empty<String>().add("y", LogicalTimestamp(1, "device-b")).remove("x")

        assertEquals(a.merge(b).elements(), b.merge(a).elements())
    }

    @Test
    fun `merge is idempotent`() {
        val set = ORSet.empty<String>().add("x", LogicalTimestamp(1, "device-a"))

        assertEquals(set.elements(), set.merge(set).elements())
    }
}
