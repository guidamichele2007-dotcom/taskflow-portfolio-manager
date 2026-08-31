package com.omnilife.core.sync

/**
 * Ties the outbox, retry engine, and connectivity/power signals together
 * (MFC §3 "Ritorno online"/"Rete a consumo", MFC-E-04 low-battery
 * suspension). No platform networking or background-task API here — this
 * is the pure decision layer ("is there anything to sync right now, and
 * should we hold off"); the platform trigger (connectivity callback,
 * WorkManager/BGTaskScheduler job) is a separate, unwritten concern this
 * sprint (same "no domain module / app-shell wiring" boundary as
 * everywhere else in Sprint 3).
 */
public class SyncScheduler(private val outbox: SyncOutboxStore) {
    private var pausedForBattery = false
    private var meteredNetwork = false

    /** MFC-E-04: background sync suspends under low battery/power-saving, resumes automatically after. */
    public fun pauseForBatteryConservation() {
        pausedForBattery = true
    }

    public fun resumeFromBatteryConservation() {
        pausedForBattery = false
    }

    /**
     * MFC §3 "Rete a consumo": on metered connections, only hot items sync
     * automatically; the rest waits for Wi-Fi/consent.
     */
    public fun setMeteredNetwork(metered: Boolean) {
        meteredNetwork = metered
    }

    public fun isPaused(): Boolean = pausedForBattery

    /** The next item this scheduler will attempt to sync right now, or `null` if nothing is eligible. */
    public fun nextEligibleItem(): OutboxItem? {
        if (pausedForBattery) return null
        val next = outbox.peekNext() ?: return null
        return if (meteredNetwork && !next.isHot) null else next
    }
}
