package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

/**
 * Background Delivery: what an OS-level background job (WorkManager/BGTaskScheduler, app-shell
 * wiring, out of this module — same boundary as `core-sync`'s `SyncScheduler`/
 * `BackgroundSyncCoordinator`) invokes periodically to process anything that became due while
 * the app wasn't foregrounded: quiet-hours-deferred requests reaching wake time, and the digest
 * reaching its scheduled delivery time.
 */
public class BackgroundDeliveryCoordinator(private val broker: NotificationBroker) {
    /** Idempotent: safe to call more often than strictly needed, a no-op when nothing is due. */
    public fun runOnce(
        now: Instant,
        zone: TimeZone,
    ) {
        broker.processDeferred(now, zone)
        broker.flushDigestIfDue(now, zone)
    }
}
