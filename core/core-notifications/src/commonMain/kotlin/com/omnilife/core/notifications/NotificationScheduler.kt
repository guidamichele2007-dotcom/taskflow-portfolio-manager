package com.omnilife.core.notifications

import kotlinx.datetime.Instant

/**
 * The low-level "at this instant, deliver this notification" primitive (TDR-26). Everything
 * above this — budget, digest, quiet hours, retry, history — lives in [NotificationBroker]/
 * [LocalNotificationService]; this class only knows how to fire a callback at (approximately) a
 * given wall-clock instant, and cancel a pending one.
 */
public expect class NotificationScheduler {
    public fun schedule(
        requestId: String,
        at: Instant,
        onFire: () -> Unit,
    )

    public fun cancel(requestId: String)

    public fun cancelAll()
}
