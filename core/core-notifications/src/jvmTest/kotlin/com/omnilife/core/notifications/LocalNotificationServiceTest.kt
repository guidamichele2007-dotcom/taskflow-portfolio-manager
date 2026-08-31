package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocalNotificationServiceTest {
    private val category = NotificationCategory("task.reminder", "task")
    private val channelSpec = NotificationChannelSpec("task.reminder", "Attività", NotificationImportance.HIGH)

    private fun request(scheduledFor: Instant) =
        NotificationRequest(
            id = "r1",
            category = category,
            priority = NotificationPriority.PROMEMORIA_UTENTE,
            entityReference = EntityReference("task-1", "task"),
            title = "t",
            body = "b",
            scheduledFor = scheduledFor,
        )

    @Test
    fun `show delivers via the real scheduler when permission is granted`() {
        val service =
            DefaultLocalNotificationService(
                NotificationScheduler(),
                NotificationChannelRegistry(),
                NotificationPermissionManager(),
            )
        val latch = CountDownLatch(1)

        service.show(request(kotlinx.datetime.Clock.System.now()), channelSpec) { latch.countDown() }

        assertTrue(latch.await(2, TimeUnit.SECONDS))
    }

    @Test
    fun `show is a no-op when permission is denied`() {
        val permissionManager =
            NotificationPermissionManager().apply { simulatedStatus = NotificationPermissionStatus.DENIED }
        val service =
            DefaultLocalNotificationService(NotificationScheduler(), NotificationChannelRegistry(), permissionManager)
        val latch = CountDownLatch(1)

        service.show(request(kotlinx.datetime.Clock.System.now()), channelSpec) { latch.countDown() }

        assertFalse(latch.await(500, TimeUnit.MILLISECONDS), "onDelivered must not run without permission")
    }

    @Test
    fun `show registers the channel before scheduling`() {
        val channelRegistry = NotificationChannelRegistry()
        val service =
            DefaultLocalNotificationService(NotificationScheduler(), channelRegistry, NotificationPermissionManager())

        service.show(request(kotlinx.datetime.Clock.System.now()), channelSpec) {}

        assertTrue(channelRegistry.registeredChannels().any { it.channelId == channelSpec.channelId })
    }
}
