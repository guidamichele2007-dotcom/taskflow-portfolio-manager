package com.omnilife.feature.task

import com.omnilife.core.common.Envelope
import kotlinx.datetime.Instant

internal fun testEnvelopeFixture(id: String): Envelope =
    Envelope(
        id = id,
        ownerAccountId = "account-1",
        schemaVersion = 1,
        createdAt = Instant.fromEpochMilliseconds(0),
        createdByDevice = "device-1",
        modifiedAt = Instant.fromEpochMilliseconds(0),
        modifiedByDevice = "device-1",
    )
