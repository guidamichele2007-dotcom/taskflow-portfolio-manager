package com.omnilife.core.sync

/**
 * Outcome of resolving one field's local/remote values — distinct from the
 * raw CRDT merge ([LwwRegister.merge]) because callers (e.g. a future
 * "conflict inbox" UI, out of this sprint's scope) need to know *that* a
 * conflict happened, not just the resolved value (Technical Architecture
 * Bible §05: "conflitti risolti automaticamente, ma tracciabili").
 */
public data class FieldResolution<T>(
    public val fieldName: String,
    public val resolved: LwwRegister<T>,
    public val hadConflict: Boolean,
)

/**
 * Per-field conflict resolution — the layer [MergeEngine] calls once per
 * field. Swappable in principle (a future module could plug in a
 * field-type-aware strategy); this sprint ships the one strategy every
 * Bible field currently needs: last-writer-wins.
 */
public interface ConflictResolver {
    /** At least one of [local]/[remote] must be non-null — there is nothing to resolve if both are absent. */
    public fun resolveField(
        fieldName: String,
        local: LwwRegister<Any?>?,
        remote: LwwRegister<Any?>?,
    ): FieldResolution<Any?>
}

public class LwwConflictResolver : ConflictResolver {
    override fun resolveField(
        fieldName: String,
        local: LwwRegister<Any?>?,
        remote: LwwRegister<Any?>?,
    ): FieldResolution<Any?> {
        requireNotNull(local ?: remote) { "At least one of local/remote must be non-null for field '$fieldName'" }
        if (local == null) return FieldResolution(fieldName, remote!!, hadConflict = false)
        if (remote == null) return FieldResolution(fieldName, local, hadConflict = false)

        val merged = LwwRegister.merge(local, remote)
        // Heuristic (no causal history is tracked): a genuine conflict is two different
        // values racing, not merely two writes of the same value at different times.
        val hadConflict = local.value != remote.value
        return FieldResolution(fieldName, merged, hadConflict)
    }
}
