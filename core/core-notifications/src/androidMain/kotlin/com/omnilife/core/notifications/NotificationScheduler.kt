package com.omnilife.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlinx.datetime.Instant

/**
 * Android `actual`: `AlarmManager.setExactAndAllowWhileIdle` (TDR-26). Not compiled/verified in
 * this sandbox (no Android SDK — README-BUILD.md §4). [onFire] cannot survive process death as a
 * captured lambda on Android — the real integration is `androidApp`'s `NotificationFireReceiver`
 * (Sprint 6), registered in the manifest for [ACTION_NOTIFICATION_FIRE] and reading the pending
 * request back out of the process-wide `AppContainer` singleton rather than relying on this
 * closure ever running.
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
        val intent =
            Intent(ACTION_NOTIFICATION_FIRE)
                .putExtra(EXTRA_REQUEST_ID, requestId)
                // Explicit-to-package (not a full component name) is enough to reach a
                // manifest-registered <receiver> despite the O+ implicit-broadcast restrictions,
                // without core-notifications needing to know androidApp's receiver class name.
                .setPackage(context.packageName)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val triggerAtMillis = at.toEpochMilliseconds()
        if (canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            // API 33+: "Alarms & reminders" is an off-by-default user-granted system setting: a
            // device where the user hasn't granted it must not crash with SecurityException — the
            // reminder still fires close to on time via the non-exact path instead of being lost.
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
        pendingIntents[requestId] = pendingIntent
    }

    public actual fun cancel(requestId: String) {
        pendingIntents.remove(requestId)?.let { alarmManager.cancel(it) }
    }

    public actual fun cancelAll() {
        pendingIntents.keys.toList().forEach { cancel(it) }
    }

    private fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    public companion object {
        public const val ACTION_NOTIFICATION_FIRE: String = "com.omnilife.core.notifications.ACTION_FIRE"
        public const val EXTRA_REQUEST_ID: String = "requestId"
    }
}
