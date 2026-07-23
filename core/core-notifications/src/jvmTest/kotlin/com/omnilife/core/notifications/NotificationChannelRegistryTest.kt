package com.omnilife.core.notifications

import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationChannelRegistryTest {
    @Test
    fun `ensureChannel registers the channel spec`() {
        val registry = NotificationChannelRegistry()
        val spec = NotificationChannelSpec("task.reminder", "Attività", NotificationImportance.HIGH)

        registry.ensureChannel(spec)

        assertEquals(listOf(spec), registry.registeredChannels())
    }

    @Test
    fun `re-registering the same channel id updates it rather than duplicating it`() {
        val registry = NotificationChannelRegistry()
        registry.ensureChannel(NotificationChannelSpec("task.reminder", "Attività", NotificationImportance.LOW))
        registry.ensureChannel(NotificationChannelSpec("task.reminder", "Attività", NotificationImportance.HIGH))

        assertEquals(1, registry.registeredChannels().size)
        assertEquals(NotificationImportance.HIGH, registry.registeredChannels().single().importance)
    }
}
