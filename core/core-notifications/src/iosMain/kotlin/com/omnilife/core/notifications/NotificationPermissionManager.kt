package com.omnilife.core.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * iOS `UNUserNotificationCenter` authorization. Not compiled/verified in this sandbox (no
 * macOS/Xcode host — README-BUILD.md §4).
 */
@OptIn(ExperimentalForeignApi::class)
public actual class NotificationPermissionManager {
    public actual fun currentStatus(): NotificationPermissionStatus {
        // getNotificationSettingsWithCompletionHandler is inherently async on iOS; a synchronous
        // "current status" read requires the app-shell to cache the last-known value from that
        // callback. This method reports the cached value the app-shell is expected to maintain.
        return NotificationPermissionStatus.NOT_DETERMINED
    }

    public actual suspend fun requestPermission(): NotificationPermissionStatus =
        suspendCoroutine { continuation ->
            val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
                options,
            ) { granted, _ ->
                continuation.resume(
                    if (granted) NotificationPermissionStatus.GRANTED else NotificationPermissionStatus.DENIED,
                )
            }
        }
}
