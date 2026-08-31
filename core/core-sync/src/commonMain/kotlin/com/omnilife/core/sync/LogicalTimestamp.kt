package com.omnilife.core.sync

/**
 * A minimal "logical version vector" (TDR-24): a per-entity/per-field
 * monotonic counter plus the device that produced it, compared
 * lexicographically (counter first, `deviceId` only as a deterministic
 * tie-break). Never wall-clock (Data Model Bible §11 §6, MFC-E-10: a wrong
 * system clock must never produce sync paradoxes).
 */
public data class LogicalTimestamp(
    public val counter: Long,
    public val deviceId: String,
) : Comparable<LogicalTimestamp> {
    override fun compareTo(other: LogicalTimestamp): Int {
        val counterComparison = counter.compareTo(other.counter)
        return if (counterComparison != 0) counterComparison else deviceId.compareTo(other.deviceId)
    }

    public fun next(byDevice: String): LogicalTimestamp = LogicalTimestamp(counter + 1, byDevice)

    public companion object {
        public fun initial(deviceId: String): LogicalTimestamp = LogicalTimestamp(0, deviceId)
    }
}
