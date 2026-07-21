package com.omnilife.core.common

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EnvelopeTest {
    private fun sampleEnvelope() =
        Envelope(
            id = "task-1",
            ownerAccountId = "account-1",
            schemaVersion = 1,
            createdAt = Instant.fromEpochMilliseconds(0),
            createdByDevice = "device-1",
            modifiedAt = Instant.fromEpochMilliseconds(0),
            modifiedByDevice = "device-1",
        )

    @Test
    fun `defaults to active lifecycle with no trashedAt`() {
        val envelope = sampleEnvelope()
        assertEquals(EntityLifecycleState.ACTIVE, envelope.lifecycleState)
        assertNull(envelope.trashedAt)
    }

    @Test
    fun `trashed envelope carries a trashedAt timestamp`() {
        val trashedAt = Instant.fromEpochMilliseconds(1_000)
        val envelope = sampleEnvelope().copy(lifecycleState = EntityLifecycleState.TRASHED, trashedAt = trashedAt)
        assertEquals(EntityLifecycleState.TRASHED, envelope.lifecycleState)
        assertEquals(trashedAt, envelope.trashedAt)
    }
}
