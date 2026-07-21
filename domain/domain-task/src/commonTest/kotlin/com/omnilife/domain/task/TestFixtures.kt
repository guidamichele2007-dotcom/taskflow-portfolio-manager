package com.omnilife.domain.task

import com.omnilife.core.common.Envelope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal fun testEnvelope(id: String = "task-1"): Envelope =
    Envelope(
        id = id,
        ownerAccountId = "account-1",
        schemaVersion = 1,
        createdAt = Instant.fromEpochMilliseconds(0),
        createdByDevice = "device-1",
        modifiedAt = Instant.fromEpochMilliseconds(0),
        modifiedByDevice = "device-1",
    )

/**
 * Shared across test files (internal is module-wide) — never redeclare
 * this locally, a same-named top-level class clashes at the bytecode level.
 */
internal class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}
