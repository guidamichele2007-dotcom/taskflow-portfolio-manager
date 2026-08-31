package com.omnilife.feature.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeWidgetRegistryTest {
    @Test
    fun `defaults to every widget kind active in HOME-R-03-inspired order`() {
        val registry = InMemoryHomeWidgetRegistry()
        assertEquals(InMemoryHomeWidgetRegistry.DEFAULT_ORDER, registry.activeWidgets())
    }

    @Test
    fun `setActive false removes exactly that kind from activeWidgets`() {
        val registry = InMemoryHomeWidgetRegistry()

        registry.setActive(HomeWidgetKind.CALENDAR_SUMMARY, active = false)

        assertFalse(HomeWidgetKind.CALENDAR_SUMMARY in registry.activeWidgets())
        assertEquals(HomeWidgetKind.entries.size - 1, registry.activeWidgets().size)
    }

    @Test
    fun `HOME-003 - reactivating a previously deactivated kind restores it at its original position`() {
        val registry = InMemoryHomeWidgetRegistry()
        registry.setActive(HomeWidgetKind.AGENDA, active = false)

        registry.setActive(HomeWidgetKind.AGENDA, active = true)

        assertEquals(InMemoryHomeWidgetRegistry.DEFAULT_ORDER, registry.activeWidgets())
    }

    @Test
    fun `reorder changes the order returned by activeWidgets`() {
        val registry = InMemoryHomeWidgetRegistry()
        val newOrder = InMemoryHomeWidgetRegistry.DEFAULT_ORDER.reversed()

        registry.reorder(newOrder)

        assertEquals(newOrder, registry.activeWidgets())
    }

    @Test
    fun `reorder preserves inactive kinds' exclusion from activeWidgets`() {
        val registry = InMemoryHomeWidgetRegistry()
        registry.setActive(HomeWidgetKind.FINANCE_SUMMARY, active = false)

        registry.reorder(InMemoryHomeWidgetRegistry.DEFAULT_ORDER.reversed())

        assertFalse(HomeWidgetKind.FINANCE_SUMMARY in registry.activeWidgets())
    }

    @Test
    fun `reorder rejects a list that is not a permutation of the existing kinds`() {
        val registry = InMemoryHomeWidgetRegistry()
        assertFailsWith<IllegalArgumentException> {
            registry.reorder(listOf(HomeWidgetKind.TODAY_OVERVIEW))
        }
    }

    @Test
    fun `activating an already-active kind is idempotent`() {
        val registry = InMemoryHomeWidgetRegistry()
        registry.setActive(HomeWidgetKind.TODAY_OVERVIEW, active = true)
        assertTrue(HomeWidgetKind.TODAY_OVERVIEW in registry.activeWidgets())
    }
}
