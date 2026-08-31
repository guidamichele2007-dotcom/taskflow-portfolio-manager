package com.omnilife.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

/**
 * Android `NotificationChannel` creation. Not compiled/verified in this sandbox (no Android SDK
 * — README-BUILD.md §4).
 */
public actual class NotificationChannelRegistry(private val context: Context) {
    public actual fun ensureChannel(spec: NotificationChannelSpec) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance =
            when (spec.importance) {
                NotificationImportance.HIGH -> NotificationManager.IMPORTANCE_HIGH
                NotificationImportance.DEFAULT -> NotificationManager.IMPORTANCE_DEFAULT
                NotificationImportance.LOW -> NotificationManager.IMPORTANCE_LOW
            }
        // createNotificationChannel is itself idempotent (recreating with the same id is a no-op
        // on Android), so no separate "already registered" check is needed here.
        manager.createNotificationChannel(NotificationChannel(spec.channelId, spec.displayName, importance))
    }
}
