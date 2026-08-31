package com.omnilife.core.notifications

/**
 * JVM tracks registered channels (testable) but has no OS notification-channel concept to
 * create (README-BUILD.md §4).
 */
public actual class NotificationChannelRegistry {
    private val registered = mutableMapOf<String, NotificationChannelSpec>()

    public actual fun ensureChannel(spec: NotificationChannelSpec) {
        registered[spec.channelId] = spec
    }

    public fun registeredChannels(): List<NotificationChannelSpec> = registered.values.toList()
}
