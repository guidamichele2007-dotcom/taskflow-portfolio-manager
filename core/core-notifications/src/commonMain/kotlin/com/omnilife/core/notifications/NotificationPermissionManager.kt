package com.omnilife.core.notifications

/**
 * NTF §2 edge case: "permesso di sistema revocato → tutte le categorie mostrano lo stato in
 * NTF-007, i contenuti restano in app (P6)" — [status] is read before every attempt to actually
 * show a notification; [DENIED] never blocks the rest of the app, only the OS-level push.
 */
public enum class NotificationPermissionStatus {
    GRANTED,
    DENIED,
    NOT_DETERMINED,
}

/**
 * Platform-specific permission query/request (Android 13+ `POST_NOTIFICATIONS`, iOS
 * `UNUserNotificationCenter` authorization). Only the JVM `actual` is exercised in this sandbox
 * (no Android SDK, no macOS/Xcode — the same gating documented for every other platform target
 * since Sprint 1, README-BUILD.md §4); JVM has no OS-level notification permission concept, so
 * its `actual` is a testable in-memory stand-in, not a real system call.
 */
public expect class NotificationPermissionManager {
    public fun currentStatus(): NotificationPermissionStatus

    public suspend fun requestPermission(): NotificationPermissionStatus
}
