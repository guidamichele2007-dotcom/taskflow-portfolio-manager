package com.omnilife.core.notifications

import kotlinx.datetime.Clock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class NotificationSchedulerTest {
    @Test
    fun `schedule fires onFire at approximately the requested instant`() {
        val scheduler = NotificationScheduler()
        val latch = CountDownLatch(1)

        scheduler.schedule("r1", Clock.System.now()) { latch.countDown() }

        assertTrue(latch.await(2, TimeUnit.SECONDS), "onFire should have run within 2 seconds")
    }

    @Test
    fun `cancel prevents a pending onFire from running`() {
        val scheduler = NotificationScheduler()
        val latch = CountDownLatch(1)

        scheduler.schedule("r1", Clock.System.now() + 1.seconds) { latch.countDown() }
        scheduler.cancel("r1")

        assertFalse(latch.await(1500, TimeUnit.MILLISECONDS), "onFire must not run after cancel")
    }

    @Test
    fun `re-scheduling the same requestId replaces the earlier pending fire`() {
        val scheduler = NotificationScheduler()
        val fired = mutableListOf<String>()
        val latch = CountDownLatch(1)

        scheduler.schedule("r1", Clock.System.now() + 2.seconds) { fired.add("first") }
        scheduler.schedule("r1", Clock.System.now()) {
            fired.add("second")
            latch.countDown()
        }

        assertTrue(latch.await(2, TimeUnit.SECONDS))
        assertEquals(listOf("second"), fired)
    }

    @Test
    fun `cancelAll cancels every pending schedule`() {
        val scheduler = NotificationScheduler()
        val latch = CountDownLatch(2)
        scheduler.schedule("r1", Clock.System.now() + 1.seconds) { latch.countDown() }
        scheduler.schedule("r2", Clock.System.now() + 1.seconds) { latch.countDown() }

        scheduler.cancelAll()

        assertFalse(latch.await(1500, TimeUnit.MILLISECONDS))
    }
}
