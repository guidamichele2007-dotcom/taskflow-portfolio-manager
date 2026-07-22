package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NetworkMonitorTest {
    @Test
    fun `defaults to online`() {
        assertTrue(ManualNetworkMonitor().isOnline())
    }

    @Test
    fun `can be constructed offline`() {
        assertFalse(ManualNetworkMonitor(initiallyOnline = false).isOnline())
    }

    @Test
    fun `setOnline updates isOnline`() {
        val monitor = ManualNetworkMonitor()
        monitor.setOnline(false)
        assertFalse(monitor.isOnline())
    }

    @Test
    fun `listeners are notified on a real transition`() {
        val monitor = ManualNetworkMonitor()
        val observed = mutableListOf<Boolean>()
        monitor.onConnectivityChanged { observed.add(it) }

        monitor.setOnline(false)
        monitor.setOnline(true)

        assertEquals(listOf(false, true), observed)
    }

    @Test
    fun `setting the same value again does not notify listeners`() {
        val monitor = ManualNetworkMonitor()
        val observed = mutableListOf<Boolean>()
        monitor.onConnectivityChanged { observed.add(it) }

        monitor.setOnline(true)

        assertTrue(observed.isEmpty())
    }
}
