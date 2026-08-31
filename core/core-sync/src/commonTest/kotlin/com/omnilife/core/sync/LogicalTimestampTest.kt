package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertTrue

class LogicalTimestampTest {
    @Test
    fun `a higher counter always wins regardless of device id`() {
        val earlier = LogicalTimestamp(1, "device-z")
        val later = LogicalTimestamp(2, "device-a")

        assertTrue(later > earlier)
    }

    @Test
    fun `deviceId breaks ties deterministically when counters are equal (not wall-clock)`() {
        val a = LogicalTimestamp(5, "device-a")
        val b = LogicalTimestamp(5, "device-b")

        assertTrue(b > a)
        assertTrue(a < b)
    }

    @Test
    fun `next increments the counter and can change the device`() {
        val original = LogicalTimestamp.initial("device-a")

        val advanced = original.next("device-b")

        assertTrue(advanced > original)
    }
}
