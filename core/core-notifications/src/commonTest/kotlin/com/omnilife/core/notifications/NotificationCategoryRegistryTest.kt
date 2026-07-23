package com.omnilife.core.notifications

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationCategoryRegistryTest {
    private val category = NotificationCategory(id = "task.reminder", moduleName = "task")

    @Test
    fun `a registered category defaults to its declared defaultEnabled`() {
        val registry = InMemoryNotificationCategoryRegistry()
        registry.register(NotificationCategory("task.reminder", "task", defaultEnabled = false))

        assertFalse(registry.isEnabled("task.reminder"))
    }

    @Test
    fun `an unknown category id defaults to enabled`() {
        val registry = InMemoryNotificationCategoryRegistry()
        assertTrue(registry.isEnabled("never-registered"))
    }

    @Test
    fun `setEnabled overrides the default`() {
        val registry = InMemoryNotificationCategoryRegistry()
        registry.register(category)

        registry.setEnabled("task.reminder", false)

        assertFalse(registry.isEnabled("task.reminder"))
    }

    @Test
    fun `NTF-006 - three consecutive ignored outcomes propose auto-disable`() {
        val registry = InMemoryNotificationCategoryRegistry()
        registry.register(category)

        repeat(3) { registry.recordOutcome("task.reminder", NotificationOutcome.IGNORATA) }

        assertTrue(registry.shouldProposeAutoDisable("task.reminder"))
    }

    @Test
    fun `two consecutive ignored outcomes do not yet propose auto-disable`() {
        val registry = InMemoryNotificationCategoryRegistry()
        registry.register(category)

        repeat(2) { registry.recordOutcome("task.reminder", NotificationOutcome.IGNORATA) }

        assertFalse(registry.shouldProposeAutoDisable("task.reminder"))
    }

    @Test
    fun `a non-ignored outcome resets the consecutive-ignore streak`() {
        val registry = InMemoryNotificationCategoryRegistry()
        registry.register(category)

        registry.recordOutcome("task.reminder", NotificationOutcome.IGNORATA)
        registry.recordOutcome("task.reminder", NotificationOutcome.IGNORATA)
        registry.recordOutcome("task.reminder", NotificationOutcome.MOSTRATA)
        registry.recordOutcome("task.reminder", NotificationOutcome.IGNORATA)
        registry.recordOutcome("task.reminder", NotificationOutcome.IGNORATA)

        assertFalse(registry.shouldProposeAutoDisable("task.reminder"))
    }

    @Test
    fun `categories lists every registered category`() {
        val registry = InMemoryNotificationCategoryRegistry()
        registry.register(category)
        registry.register(NotificationCategory("habit.streak-risk", "habit"))

        assertEquals(2, registry.categories().size)
    }
}
