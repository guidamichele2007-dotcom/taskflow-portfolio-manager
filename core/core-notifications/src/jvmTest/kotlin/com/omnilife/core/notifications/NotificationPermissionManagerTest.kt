package com.omnilife.core.notifications

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationPermissionManagerTest {
    @Test
    fun `defaults to GRANTED`() {
        assertEquals(NotificationPermissionStatus.GRANTED, NotificationPermissionManager().currentStatus())
    }

    @Test
    fun `currentStatus reflects the simulated status`() {
        val manager = NotificationPermissionManager()
        manager.simulatedStatus = NotificationPermissionStatus.DENIED
        assertEquals(NotificationPermissionStatus.DENIED, manager.currentStatus())
    }

    @Test
    fun `requestPermission resolves NOT_DETERMINED to GRANTED`() =
        runTest {
            val manager = NotificationPermissionManager()
            manager.simulatedStatus = NotificationPermissionStatus.NOT_DETERMINED

            val result = manager.requestPermission()

            assertEquals(NotificationPermissionStatus.GRANTED, result)
        }

    @Test
    fun `requestPermission does not override an explicit DENIED`() =
        runTest {
            val manager = NotificationPermissionManager()
            manager.simulatedStatus = NotificationPermissionStatus.DENIED

            val result = manager.requestPermission()

            assertEquals(NotificationPermissionStatus.DENIED, result)
        }
}
