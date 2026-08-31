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

    public fun onConnectivityChanged(listener: (Boolean) -> Unit): NetworkMonitorSubscription
}

/**
 * Sprint 6: mirrors [SyncStateSubscription] (TDR-34) — found during this sprint's leak audit that
 * [NetworkMonitor.onConnectivityChanged] had the exact same unsubscribe-less shape the Sprint 5
 * leak fix targeted, just never applied here (no production caller exists yet, so it hadn't bitten
 * anyone, but the next caller to wire this up would have inherited the same leak).
 */
public fun interface NetworkMonitorSubscription {
    public fun cancel()
}

public class ManualNetworkMonitor(initiallyOnline: Boolean = true) : NetworkMonitor {
    private var online = initiallyOnline
    private val listeners = mutableListOf<(Boolean) -> Unit>()

    override fun isOnline(): Boolean = online

    override fun onConnectivityChanged(listener: (Boolean) -> Unit): NetworkMonitorSubscription {
        listeners.add(listener)
        return NetworkMonitorSubscription { listeners.remove(listener) }
    }

    /** Test/manual-control seam — a real platform actual would call this from its own callback. */
    public fun setOnline(value: Boolean) {
        if (online == value) return
        online = value
        listeners.forEach { it(value) }
    }
}
