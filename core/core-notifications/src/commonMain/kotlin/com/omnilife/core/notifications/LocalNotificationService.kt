package com.omnilife.core.notifications

import kotlinx.coroutines.delay

/**
 * NTF-001 §2 "generate localmente": the concrete act of showing one local notification —
 * composes [NotificationScheduler] (fire-at-instant), [NotificationChannelRegistry] (Android
 * channel setup), and [NotificationPermissionManager] (NTF §2 edge case, P6: "permesso di
 * sistema revocato → ... i contenuti restano in app" — [show] is simply a no-op when denied,
 * never an error the caller must handle).
 */
public interface LocalNotificationService {
    public fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    )

    public fun cancel(requestId: String)
}

public class DefaultLocalNotificationService(
    private val scheduler: NotificationScheduler,
    private val channelRegistry: NotificationChannelRegistry,
    private val permissionManager: NotificationPermissionManager,
) : LocalNotificationService {
    override fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    ) {
        if (permissionManager.currentStatus() != NotificationPermissionStatus.GRANTED) return
        channelRegistry.ensureChannel(channel)
        scheduler.schedule(request.id, request.scheduledFor) {
            onDelivered(request.copy(state = NotificationState.MOSTRATA, outcome = NotificationOutcome.MOSTRATA))
        }
    }

    override fun cancel(requestId: String) {
        scheduler.cancel(requestId)
    }
}

/**
 * Retry Logic: wraps [LocalNotificationService.show] so a transient failure at the platform
 * boundary (the scheduler call itself throwing — a real, if rare, platform API failure) retries
 * with [NotificationRetryEngine]'s backoff instead of silently dropping the notification.
 */
public suspend fun LocalNotificationService.showWithRetry(
    request: NotificationRequest,
    channel: NotificationChannelSpec,
    onDelivered: (NotificationRequest) -> Unit,
) {
    var attempt = 0
    while (true) {
        try {
            show(request, channel, onDelivered)
            return
        } catch (
            @Suppress("TooGenericExceptionCaught") schedulingError: Exception,
        ) {
            // Deliberately broad: the platform scheduler call is the unpredictable boundary here,
            // any exception it throws must go through backoff, never crash the caller.
            if (NotificationRetryEngine.hasExhaustedRetries(attempt)) throw schedulingError
            delay(NotificationRetryEngine.delayForAttempt(attempt))
            attempt++
        }
    }
}
