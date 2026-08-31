package com.omnilife.core.notifications

/** Android's notification-channel importance concept, generalized so non-Android actuals can no-op meaningfully. */
public enum class NotificationImportance {
    HIGH,
    DEFAULT,
    LOW,
}

public data class NotificationChannelSpec(
    public val channelId: String,
    public val displayName: String,
    public val importance: NotificationImportance,
)

/**
 * Android requires every notification to declare a channel before it can be shown (channels are
 * created once, never per-notification); iOS/JVM have no equivalent concept, so their `actual`s
 * are no-ops — the [NotificationChannelSpec] contract still lets [NotificationCategory]-to-channel
 * mapping stay a single, platform-agnostic decision in [LocalNotificationService].
 */
public expect class NotificationChannelRegistry {
    public fun ensureChannel(spec: NotificationChannelSpec)
}
