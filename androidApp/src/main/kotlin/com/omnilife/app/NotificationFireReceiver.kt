package com.omnilife.app

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.omnilife.core.notifications.NotificationOutcome
import com.omnilife.core.notifications.NotificationPermissionStatus
import com.omnilife.core.notifications.NotificationScheduler

/**
 * Sprint 6: closes the gap `NotificationScheduler`'s own doc comment flagged (TDR-26) — Android
 * fires a scheduled reminder as a broadcast `Intent` specifically so something can still catch it
 * once the in-process `onFire` closure captured at schedule-time is gone (Activity recreated, or
 * the process killed and later restarted by the alarm itself). Registered in the manifest for
 * [NotificationScheduler.ACTION_NOTIFICATION_FIRE]; reads the pending request straight out of the
 * process-wide [OmniLifeApplication.container] and posts the real system notification via
 * [NotificationManagerCompat] — until this sprint, nothing in this codebase ever called that API,
 * so no local notification actually reached the system tray on Android, on or off this receiver.
 *
 * **Known remaining limit** (documented, not silently accepted as fixed): this only works while
 * the app's process is still alive. `NotificationHistoryStore` is in-memory only (a Sprint 5
 * residual risk, still open) — a reminder that fires after Android has fully killed the process
 * has nothing to look up and is silently lost rather than shown late or wrong. A real fix needs a
 * persisted request store; out of this sprint's scope (see `sprint6_report.md` §7).
 */
public class NotificationFireReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val requestId = intent.getStringExtra(NotificationScheduler.EXTRA_REQUEST_ID) ?: return
        val container = (context.applicationContext as OmniLifeApplication).container
        if (container.notificationPermissionManager.currentStatus() != NotificationPermissionStatus.GRANTED) return
        val request = container.notificationHistoryStore.findById(requestId) ?: return

        val notification =
            NotificationCompat.Builder(context, request.category.id)
                .setContentTitle(request.title)
                .setContentText(request.body)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true)
                .setContentIntent(contentIntentFor(context, requestId))
                .build()
        NotificationManagerCompat.from(context).notify(requestId.hashCode(), notification)
        container.notificationBroker.recordOutcome(request, NotificationOutcome.MOSTRATA)
    }

    /**
     * Deep link back into [MainActivity] on tap (NTF §... "azionata" outcome path). MVP Release
     * 1.0: now opens the specific task's Detail sheet, not just the app generically — `requestId`
     * doubles as the task id (`TaskNotificationBridge`'s own design: one reminder per task, keyed
     * by the task's id), passed as [MainActivity.EXTRA_OPEN_TASK_ID] and consumed by
     * `MainActivity`/`AppShell`. `singleTask` launch mode (manifest) + `FLAG_ACTIVITY_CLEAR_TOP`
     * ensure this re-delivers into the already-running Activity via `onNewIntent` instead of
     * spawning a second instance when the app is already open.
     */
    private fun contentIntentFor(
        context: Context,
        requestId: String,
    ): PendingIntent {
        val activityIntent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(MainActivity.EXTRA_OPEN_TASK_ID, requestId)
            }
        return PendingIntent.getActivity(
            context,
            requestId.hashCode(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
