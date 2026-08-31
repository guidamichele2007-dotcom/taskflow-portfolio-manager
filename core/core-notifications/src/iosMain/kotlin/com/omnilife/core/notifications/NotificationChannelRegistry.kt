package com.omnilife.core.notifications

/** iOS has no channel concept (importance is set per-notification, not per-channel) — a no-op actual. */
public actual class NotificationChannelRegistry {
    public actual fun ensureChannel(spec: NotificationChannelSpec) {
        // Intentional no-op: UNNotificationRequest carries its own priority via
        // interruptionLevel, there is nothing to pre-register on iOS.
    }
}
