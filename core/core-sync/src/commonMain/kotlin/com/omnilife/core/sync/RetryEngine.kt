package com.omnilife.core.sync

import kotlinx.datetime.Instant
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/**
 * Exponential backoff for the offline queue's retry cadence (TDR-24 — the
 * Bible mandates "backoff automatico" and a 72h persistent-failure
 * threshold, MFC §3, but explicitly defers the concrete schedule to
 * implementation, Technical Architecture Bible §05 §6). Base/factor/cap are
 * this module's own documented choice, not derived from any Bible number.
 */
public object RetryEngine {
    private val baseDelay = 5.seconds
    private const val FACTOR = 2.0
    private val maxDelay = 1.hours

    /** MFC §3: "dopo 72h di fallimenti persistenti, notifica locale informativa". */
    public val persistentFailureThreshold: Duration = 72.hours

    public fun delayForAttempt(attempt: Int): Duration {
        require(attempt >= 0) { "attempt must be >= 0, was $attempt" }
        val scaled = baseDelay * FACTOR.pow(attempt)
        return if (scaled > maxDelay) maxDelay else scaled
    }

    public fun hasPersistentFailure(
        firstFailureAt: Instant,
        now: Instant,
    ): Boolean = (now - firstFailureAt) >= persistentFailureThreshold
}
