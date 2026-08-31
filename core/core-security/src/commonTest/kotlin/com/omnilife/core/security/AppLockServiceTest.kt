package com.omnilife.core.security

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** Deterministic, settable clock — the same pattern `domain-task` uses for its own tests. */
private class FixedClock(var now: Instant) : Clock {
    override fun now(): Instant = now
}

class AppLockServiceTest {
    @Test
    fun `starts locked before any unlock`() {
        val service = AppLockService()

        assertTrue(service.isLocked())
    }

    @Test
    fun `unlocks after recordSuccessfulUnlock`() {
        val service = AppLockService()

        service.recordSuccessfulUnlock()

        assertFalse(service.isLocked())
    }

    @Test
    fun `SEC-AC-01 - stays unlocked if foregrounded before the configured timeout elapses`() {
        val clock = FixedClock(Instant.fromEpochMilliseconds(0))
        val service = AppLockService(clock = clock, timeout = AppLockTimeout.FIVE_MINUTES)
        service.recordSuccessfulUnlock()

        service.recordBackgrounded()
        clock.now += 4.minutes
        service.recordForegrounded()

        assertFalse(service.isLocked())
    }

    @Test
    fun `SEC-AC-01 - relocks if foregrounded after the configured timeout elapses`() {
        val clock = FixedClock(Instant.fromEpochMilliseconds(0))
        val service = AppLockService(clock = clock, timeout = AppLockTimeout.FIVE_MINUTES)
        service.recordSuccessfulUnlock()

        service.recordBackgrounded()
        clock.now += 6.minutes
        service.recordForegrounded()

        assertTrue(service.isLocked())
    }

    @Test
    fun `IMMEDIATE timeout always relocks on foreground, however brief`() {
        val clock = FixedClock(Instant.fromEpochMilliseconds(0))
        val service = AppLockService(clock = clock, timeout = AppLockTimeout.IMMEDIATE)
        service.recordSuccessfulUnlock()

        service.recordBackgrounded()
        clock.now += 1.seconds
        service.recordForegrounded()

        assertTrue(service.isLocked())
    }

    @Test
    fun `MFC-R-22 - sensitive modules are obscured while locked, non-sensitive modules stay visible`() {
        val service = AppLockService()
        service.markSensitiveModule("finance")

        assertTrue(service.isLocked())
        assertFalse(service.isModuleVisible("finance"))
        assertTrue(service.isModuleVisible("task"))
    }

    @Test
    fun `every module is visible once unlocked, including sensitive ones`() {
        val service = AppLockService()
        service.markSensitiveModule("finance")
        service.recordSuccessfulUnlock()

        assertTrue(service.isModuleVisible("finance"))
    }

    @Test
    fun `unmarkSensitiveModule restores visibility while locked`() {
        val service = AppLockService()
        service.markSensitiveModule("finance")
        service.unmarkSensitiveModule("finance")

        assertTrue(service.isModuleVisible("finance"))
    }
}
