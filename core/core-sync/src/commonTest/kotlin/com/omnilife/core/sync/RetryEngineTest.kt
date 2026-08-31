package com.omnilife.core.sync

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class RetryEngineTest {
    @Test
    fun `delay grows exponentially with the attempt number`() {
        assertEquals(5.seconds, RetryEngine.delayForAttempt(0))
        assertEquals(10.seconds, RetryEngine.delayForAttempt(1))
        assertEquals(20.seconds, RetryEngine.delayForAttempt(2))
    }

    @Test
    fun `delay is capped at the maximum, never growing unbounded`() {
        val delay = RetryEngine.delayForAttempt(20)

        assertEquals(1.hours, delay)
    }

    @Test
    fun `MFC section 3 - persistent failure is flagged only after 72 hours`() {
        val start = Instant.fromEpochMilliseconds(0)

        assertFalse(RetryEngine.hasPersistentFailure(start, start + 71.hours))
        assertTrue(RetryEngine.hasPersistentFailure(start, start + 72.hours))
    }
}
