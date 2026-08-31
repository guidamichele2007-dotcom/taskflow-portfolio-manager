package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConflictResolverTest {
    private val resolver = LwwConflictResolver()

    @Test
    fun `resolving when only local is present returns local without a conflict`() {
        val local = LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a"))

        val resolution = resolver.resolveField("title", local, null)

        assertEquals(local, resolution.resolved)
        assertFalse(resolution.hadConflict)
    }

    @Test
    fun `resolving when only remote is present returns remote without a conflict`() {
        val remote = LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-b"))

        val resolution = resolver.resolveField("title", null, remote)

        assertEquals(remote, resolution.resolved)
        assertFalse(resolution.hadConflict)
    }

    @Test
    fun `resolving two identical values is not a conflict even at different timestamps`() {
        val local = LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a"))
        val remote = LwwRegister<Any?>("Buy milk", LogicalTimestamp(2, "device-b"))

        val resolution = resolver.resolveField("title", local, remote)

        assertFalse(resolution.hadConflict)
        assertEquals(remote, resolution.resolved)
    }

    @Test
    fun `resolving two different values is a conflict, resolved by LWW`() {
        val local = LwwRegister<Any?>("Buy milk", LogicalTimestamp(1, "device-a"))
        val remote = LwwRegister<Any?>("Buy bread", LogicalTimestamp(2, "device-b"))

        val resolution = resolver.resolveField("title", local, remote)

        assertTrue(resolution.hadConflict)
        assertEquals(remote, resolution.resolved)
    }

    @Test
    fun `resolving with both null throws`() {
        assertFailsWith<IllegalArgumentException> {
            resolver.resolveField("title", null, null)
        }
    }
}
