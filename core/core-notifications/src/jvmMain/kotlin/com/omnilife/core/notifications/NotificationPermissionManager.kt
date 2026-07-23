package com.omnilife.core.notifications

/**
 * JVM has no OS-level notification permission concept — this is a settable in-memory
 * stand-in ([simulatedStatus]) so [NotificationBroker] and friends are fully testable in this
 * sandbox without a real platform call (README-BUILD.md §4).
 */
public actual class NotificationPermissionManager {
    public var simulatedStatus: NotificationPermissionStatus = NotificationPermissionStatus.GRANTED

    public actual fun currentStatus(): NotificationPermissionStatus = simulatedStatus

    public actual suspend fun requestPermission(): NotificationPermissionStatus {
        if (simulatedStatus == NotificationPermissionStatus.NOT_DETERMINED) {
            simulatedStatus = NotificationPermissionStatus.GRANTED
        }
        return simulatedStatus
    }
}
