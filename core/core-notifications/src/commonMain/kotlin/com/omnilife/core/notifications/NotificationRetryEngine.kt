package com.omnilife.core.notifications

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Backoff for a *delivery attempt* that failed at the platform boundary (e.g. the OS scheduler
 * call itself threw) — deliberately separate from `core-sync`'s `RetryEngine`: this module must
 * stay completely independent and reusable on its own (explicit requirement of this sprint), and
 * a notification delivery retry is a much shorter/faster cadence than a sync retry (a failed
 * local notification call should retry in seconds, not hours — there's no 72h "persistent
 * failure" concept here, a delivery either succeeds shortly or the moment has passed).
 */
public object NotificationRetryEngine {
    private val baseDelay = 2.seconds
    private const val FACTOR = 2.0
    private val maxDelay = 5.minutes
    private const val MAX_ATTEMPTS = 5

    public fun delayForAttempt(attempt: Int): Duration {
        require(attempt >= 0) { "attempt must be >= 0, was $attempt" }
        var scaled = baseDelay
        repeat(attempt) { scaled *= FACTOR }
        return if (scaled > maxDelay) maxDelay else scaled
    }

    /** After [MAX_ATTEMPTS] failed attempts, stop retrying — a stuck delivery call should never retry forever. */
    public fun hasExhaustedRetries(attempt: Int): Boolean = attempt >= MAX_ATTEMPTS
}
