package com.omnilife.core.sync

/**
 * Per-entity version bookkeeping (Technical Architecture Bible §05 —
 * "sincronizzazione incrementale": only entities whose version has
 * advanced since the last successful sync are considered dirty). One
 * [LogicalTimestamp] per entity, not a single global clock — a device
 * advances different entities at different rates.
 */
public interface VersionManager {
    public fun currentVersion(entityId: String): LogicalTimestamp?

    /** No-op if [version] is not strictly newer than what's already recorded (idempotent). */
    public fun recordVersion(
        entityId: String,
        version: LogicalTimestamp,
    )

    public fun isNewerThanRecorded(
        entityId: String,
        candidate: LogicalTimestamp,
    ): Boolean
}

public class InMemoryVersionManager : VersionManager {
    private val versions = mutableMapOf<String, LogicalTimestamp>()

    override fun currentVersion(entityId: String): LogicalTimestamp? = versions[entityId]

    override fun recordVersion(
        entityId: String,
        version: LogicalTimestamp,
    ) {
        val existing = versions[entityId]
        if (existing == null || version > existing) {
            versions[entityId] = version
        }
    }

    override fun isNewerThanRecorded(
        entityId: String,
        candidate: LogicalTimestamp,
    ): Boolean {
        val existing = versions[entityId] ?: return true
        return candidate > existing
    }
}
