package com.omnilife.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.launch

/**
 * MVP Release 1.0: `AlarmManager` clears every pending alarm when the device reboots — Android's
 * own documented behavior, not a bug in this app — so without this receiver, every scheduled task
 * reminder would silently vanish the moment the user restarts their phone, with nothing telling
 * them it happened. `goAsync()` + a coroutine on the process-wide [AppContainer.appScope]: the
 * system only guarantees this receiver a few seconds of execution, `goAsync` extends that past
 * `onReceive` returning without blocking it synchronously.
 */
public class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val container = (context.applicationContext as OmniLifeApplication).container
        val pendingResult = goAsync()
        container.appScope.launch {
            try {
                container.notificationBridge.reconcileAll()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
