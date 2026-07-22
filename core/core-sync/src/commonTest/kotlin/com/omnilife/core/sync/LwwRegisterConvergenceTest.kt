package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals

/** MFC-R-08/MFC §3: "l'ordine di arrivo non altera il risultato finale" — verified, not just asserted. */
class LwwRegisterConvergenceTest {
    private val early = LwwRegister("v1", LogicalTimestamp(1, "device-a"))
    private val late = LwwRegister("v2", LogicalTimestamp(2, "device-b"))

    @Test
    fun `the later timestamp always wins`() {
        assertEquals(late, LwwRegister.merge(early, late))
        assertEquals(late, LwwRegister.merge(late, early))
    }

    @Test
    fun `merge is commutative`() {
        assertEquals(LwwRegister.merge(early, late), LwwRegister.merge(late, early))
    }

    @Test
    fun `merge is idempotent`() {
        val merged = LwwRegister.merge(early, late)
        assertEquals(merged, LwwRegister.merge(merged, merged))
    }

    @Test
    fun `merging a register with itself returns the same value`() {
        assertEquals(early, LwwRegister.merge(early, early))
    }

    @Test
    fun `a lower counter never overwrites a higher one, even from a lexicographically later device`() {
        val fromDeviceA = LwwRegister("newer", LogicalTimestamp(10, "device-a"))
        val fromDeviceZ = LwwRegister("older", LogicalTimestamp(1, "device-z"))

        assertEquals(fromDeviceA, LwwRegister.merge(fromDeviceA, fromDeviceZ))
    }
}
