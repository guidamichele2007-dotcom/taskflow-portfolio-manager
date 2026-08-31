package com.omnilife.core.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Android 13+ `POST_NOTIFICATIONS` runtime permission. Not compiled/verified in this sandbox
 * (no Android SDK — README-BUILD.md §4).
 */
public actual class NotificationPermissionManager(private val context: Context) {
    public actual fun currentStatus(): NotificationPermissionStatus {
        val granted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        return if (granted) NotificationPermissionStatus.GRANTED else NotificationPermissionStatus.DENIED
    }

    /**
     * The actual runtime permission dialog must be launched from an Activity
     * (`ActivityResultContracts.RequestPermission`); this class only reports the resulting
     * status, it does not own the launcher itself (an app-shell concern, out of this module).
     */
    public actual suspend fun requestPermission(): NotificationPermissionStatus = currentStatus()
}
