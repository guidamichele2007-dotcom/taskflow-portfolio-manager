package com.omnilife.core.sync

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SyncStateManagerTest {
    @Test
    fun `starts idle with no history`() {
        val manager = InMemorySyncStateManager()
        val state = manager.current()

        assertEquals(SyncPhase.IDLE, state.phase)
        assertNull(state.lastSuccessfulSyncAt)
        assertNull(state.lastError)
    }

    @Test
    fun `transitionTo updates only the phase`() {
        val manager = InMemorySyncStateManager()
        manager.transitionTo(SyncPhase.SYNCING)
        assertEquals(SyncPhase.SYNCING, manager.current().phase)
    }

    @Test
    fun `recordSuccess clears any prior error and stamps the sync time`() {
        val manager = InMemorySyncStateManager()
        manager.recordError("boom", pendingCount = 3)

        val now = Instant.fromEpochSeconds(1_000)
        manager.recordSuccess(now, pendingCount = 0)

        val state = manager.current()
        assertEquals(SyncPhase.IDLE, state.phase)
        assertEquals(now, state.lastSuccessfulSyncAt)
        assertNull(state.lastError)
        assertEquals(0, state.pendingCount)
    }

    @Test
    fun `recordError transitions to ERROR and keeps the message`() {
        val manager = InMemorySyncStateManager()
        manager.recordError("network unreachable", pendingCount = 5)

        val state = manager.current()
        assertEquals(SyncPhase.ERROR, state.phase)
        assertEquals("network unreachable", state.lastError)
        assertEquals(5, state.pendingCount)
    }

    @Test
    fun `observers are notified on every state change`() {
        val manager = InMemorySyncStateManager()
        val phases = mutableListOf<SyncPhase>()
        manager.observe { phases.add(it.phase) }

        manager.transitionTo(SyncPhase.SYNCING)
        manager.recordSuccess(Instant.fromEpochSeconds(1), pendingCount = 0)

        assertEquals(listOf(SyncPhase.SYNCING, SyncPhase.IDLE), phases)
    }

    @Test
    fun `cancelling the returned subscription stops further notifications`() {
        val manager = InMemorySyncStateManager()
        val phases = mutableListOf<SyncPhase>()
        val subscription = manager.observe { phases.add(it.phase) }

        manager.transitionTo(SyncPhase.SYNCING)
        subscription.cancel()
        manager.recordSuccess(Instant.fromEpochSeconds(1), pendingCount = 0)
        manager.recordError("boom", pendingCount = 1)

        assertEquals(listOf(SyncPhase.SYNCING), phases)
    }

    @Test
    fun `updatePendingCount changes only the count, never the phase or error`() {
        val manager = InMemorySyncStateManager()
        manager.recordError("boom", pendingCount = 5)

        manager.updatePendingCount(9)

        val state = manager.current()
        assertEquals(9, state.pendingCount)
        assertEquals(SyncPhase.ERROR, state.phase)
        assertEquals("boom", state.lastError)
    }

    @Test
    fun `updatePendingCount notifies observers`() {
        val manager = InMemorySyncStateManager()
        val counts = mutableListOf<Int>()
        manager.observe { counts.add(it.pendingCount) }

        manager.updatePendingCount(3)

        assertEquals(listOf(3), counts)
    }

    @Test
    fun `cancelling one subscription leaves other observers of the same manager intact`() {
        val manager = InMemorySyncStateManager()
        val cancelledPhases = mutableListOf<SyncPhase>()
        val survivingPhases = mutableListOf<SyncPhase>()
        val subscription = manager.observe { cancelledPhases.add(it.phase) }
        manager.observe { survivingPhases.add(it.phase) }

        subscription.cancel()
        manager.transitionTo(SyncPhase.SYNCING)

        assertEquals(emptyList(), cancelledPhases)
        assertEquals(listOf(SyncPhase.SYNCING), survivingPhases)
    }
}
