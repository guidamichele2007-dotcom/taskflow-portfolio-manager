package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Data Model Bible §11 §3: "l'esecuzione di una ricorrenza non avviene mai due volte per lo stesso periodo". */
class RecurrenceIdempotencyTest {
    @Test
    fun `the same recurrence-rule and period is only ever generated once`() {
        val store = InMemoryRecurrenceOccurrenceStore()
        val key = RecurrenceOccurrenceKey("rule-1", "2026-07-22")

        assertTrue(store.markGeneratedIfAbsent(key))
        assertFalse(store.markGeneratedIfAbsent(key))
    }

    @Test
    fun `two different devices computing the same period independently still dedupe (key has no device component)`() {
        val store = InMemoryRecurrenceOccurrenceStore()
        val keyFromDeviceA = RecurrenceOccurrenceKey("rule-1", "2026-07-22")
        val keyFromDeviceB = RecurrenceOccurrenceKey("rule-1", "2026-07-22")

        assertTrue(store.markGeneratedIfAbsent(keyFromDeviceA))
        assertFalse(store.markGeneratedIfAbsent(keyFromDeviceB))
    }

    @Test
    fun `different periods of the same rule are independent`() {
        val store = InMemoryRecurrenceOccurrenceStore()

        assertTrue(store.markGeneratedIfAbsent(RecurrenceOccurrenceKey("rule-1", "2026-07-22")))
        assertTrue(store.markGeneratedIfAbsent(RecurrenceOccurrenceKey("rule-1", "2026-07-29")))
    }
}
