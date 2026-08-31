package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SyncSchedulerTest {
    @Test
    fun `MFC-E-04 - nothing syncs while paused for battery conservation`() {
        val outbox = InMemorySyncOutboxStore()
        outbox.enqueue(OutboxItem("item-1", byteArrayOf(1), LogicalTimestamp(1, "device-a"), isHot = true))
        val scheduler = SyncScheduler(outbox)

        scheduler.pauseForBatteryConservation()

        assertNull(scheduler.nextEligibleItem())
    }

    @Test
    fun `resuming allows syncing again`() {
        val outbox = InMemorySyncOutboxStore()
        outbox.enqueue(OutboxItem("item-1", byteArrayOf(1), LogicalTimestamp(1, "device-a"), isHot = true))
        val scheduler = SyncScheduler(outbox)
        scheduler.pauseForBatteryConservation()

        scheduler.resumeFromBatteryConservation()

        assertNotNull(scheduler.nextEligibleItem())
    }

    @Test
    fun `MFC section 3 metered network - only hot items are eligible, cold items wait`() {
        val outbox = InMemorySyncOutboxStore()
        outbox.enqueue(OutboxItem("cold-item", byteArrayOf(1), LogicalTimestamp(1, "device-a"), isHot = false))
        val scheduler = SyncScheduler(outbox)

        scheduler.setMeteredNetwork(true)

        assertNull(scheduler.nextEligibleItem())
    }

    @Test
    fun `hot items still sync on a metered network`() {
        val outbox = InMemorySyncOutboxStore()
        outbox.enqueue(OutboxItem("hot-item", byteArrayOf(1), LogicalTimestamp(1, "device-a"), isHot = true))
        val scheduler = SyncScheduler(outbox)

        scheduler.setMeteredNetwork(true)

        assertNotNull(scheduler.nextEligibleItem())
    }
}
