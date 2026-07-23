package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public enum class SmartRescheduleDecision {
    SHOW_AT_WAKE,
    EXPIRED,
}

/**
 * NTF-004/NTF-AC-03: "un promemoria delle 23 per le 23 è morto alle 8" — a notification
 * suppressed by quiet hours reappears at wake time only if it's still relevant, otherwise it's
 * dropped as [NotificationState.SCADUTA_DI_SIGNIFICATO] (visible in
 * [NotificationHistoryStore]/the in-app center, never re-shown as a push). [relevanceWindow] (4h,
 * TDR-29) is this module's own choice — the Bible states the rule but not a concrete duration.
 */
public object SmartRescheduler {
    public val relevanceWindow: Duration = 4.hours

    public fun decide(
        request: NotificationRequest,
        wakeTime: Instant,
    ): SmartRescheduleDecision =
        if (wakeTime - request.scheduledFor > relevanceWindow) {
            SmartRescheduleDecision.EXPIRED
        } else {
            SmartRescheduleDecision.SHOW_AT_WAKE
        }
}
