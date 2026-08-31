package com.omnilife.core.notifications

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class NotificationRetryEngineTest {
    @Test
    fun `delay grows exponentially from the base delay`() {
        assertEquals(2.seconds, NotificationRetryEngine.delayForAttempt(0))
        assertEquals(4.seconds, NotificationRetryEngine.delayForAttempt(1))
        assertEquals(8.seconds, NotificationRetryEngine.delayForAttempt(2))
    }

    @Test
    fun `delay is capped at the max delay`() {
        assertEquals(5.minutes, NotificationRetryEngine.delayForAttempt(20))
    }

    @Test
    fun `a negative attempt is rejected`() {
        assertFailsWith<IllegalArgumentException> { NotificationRetryEngine.delayForAttempt(-1) }
    }

    @Test
    fun `hasExhaustedRetries is false below the max attempt count, true at and beyond it`() {
        assertFalse(NotificationRetryEngine.hasExhaustedRetries(0))
        assertFalse(NotificationRetryEngine.hasExhaustedRetries(4))
        assertTrue(NotificationRetryEngine.hasExhaustedRetries(5))
        assertTrue(NotificationRetryEngine.hasExhaustedRetries(6))
    }
}
