package com.omnilife.core.sync

/**
 * Connectivity signal the [SyncScheduler]/[BackgroundSyncCoordinator] read
 * before attempting a sync round (MFC §3: sync only while connected;
 * metered-network awareness is already [SyncScheduler.setMeteredNetwork]'s
 * job). Platform connectivity APIs (Android ConnectivityManager, iOS
 * NWPathMonitor) are a Sprint 4 concern — see sprint3_report.md; this
 * sprint ships the manual, directly testable implementation every other
 * component here is built and verified against.
 */
public interface NetworkMonitor {
    public fun isOnline(): Boolean

    public fun onConnectivityChanged(listener: (Boolean) -> Unit)
}

public class ManualNetworkMonitor(initiallyOnline: Boolean = true) : NetworkMonitor {
    private var online = initiallyOnline
    private val listeners = mutableListOf<(Boolean) -> Unit>()

    override fun isOnline(): Boolean = online

    override fun onConnectivityChanged(listener: (Boolean) -> Unit) {
        listeners.add(listener)
    }

    /** Test/manual-control seam — a real platform actual would call this from its own callback. */
    public fun setOnline(value: Boolean) {
        if (online == value) return
        online = value
        listeners.forEach { it(value) }
    }
}
