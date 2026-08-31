package com.omnilife.core.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import platform.Foundation.NSTimeInterval
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

/**
 * iOS `actual`: `UNUserNotificationCenter` + `UNTimeIntervalNotificationTrigger` (TDR-26). Not
 * compiled/verified in this sandbox (no macOS/Xcode host — README-BUILD.md §4). [onFire] is not
 * actually deliverable this way — iOS shows the system notification itself via the trigger; a
 * real integration fires [onFire] from a `UNUserNotificationCenterDelegate` callback when the
 * user interacts with it, which is app-shell wiring, out of this module. This `actual` schedules
 * the OS-level alert; it does not attempt to bridge [onFire] to a foreground callback.
 */
@OptIn(ExperimentalForeignApi::class)
public actual class NotificationScheduler {
    public actual fun schedule(
        requestId: String,
        at: Instant,
        onFire: () -> Unit,
    ) {
        cancel(requestId)
        val secondsFromNow: NSTimeInterval = (at - Clock.System.now()).inWholeSeconds.toDouble().coerceAtLeast(1.0)
        val content = UNMutableNotificationContent()
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(secondsFromNow, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(requestId, content, trigger)
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request, null)
    }

    public actual fun cancel(requestId: String) {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf(requestId))
    }

    public actual fun cancelAll() {
        UNUserNotificationCenter.currentNotificationCenter().removeAllPendingNotificationRequests()
    }
}
