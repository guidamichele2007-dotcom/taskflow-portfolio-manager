package com.omnilife.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.datetime.Instant

/**
 * Android `actual`: `AlarmManager.setExactAndAllowWhileIdle` (TDR-26). Not compiled/verified in
 * this sandbox (no Android SDK — README-BUILD.md §4). [onFire] cannot survive process death as a
 * captured lambda on Android — a real integration needs a `BroadcastReceiver` that re-resolves
 * `requestId` to its handler; that receiver is app-shell wiring, out of this module.
 */
public actual class NotificationScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val pendingIntents = mutableMapOf<String, PendingIntent>()

    public actual fun schedule(
        requestId: String,
        at: Instant,
        onFire: () -> Unit,
    ) {
        cancel(requestId)
        val intent = Intent(ACTION_NOTIFICATION_FIRE).putExtra(EXTRA_REQUEST_ID, requestId)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at.toEpochMilliseconds(), pendingIntent)
        pendingIntents[requestId] = pendingIntent
    }

    public actual fun cancel(requestId: String) {
        pendingIntents.remove(requestId)?.let { alarmManager.cancel(it) }
    }

    public actual fun cancelAll() {
        pendingIntents.keys.toList().forEach { cancel(it) }
    }

    private companion object {
        const val ACTION_NOTIFICATION_FIRE = "com.omnilife.core.notifications.ACTION_FIRE"
        const val EXTRA_REQUEST_ID = "requestId"
    }
}
