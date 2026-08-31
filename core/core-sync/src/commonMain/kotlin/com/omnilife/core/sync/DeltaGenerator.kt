package com.omnilife.core.sync

/**
 * Field-level delta between two states of the same entity's field map
 * (Technical Architecture Bible §05 — "sincronizzazione incrementale":
 * only changed fields cross the wire, not the whole entity). Built
 * directly on [LwwRegister]'s existing timestamp ordering rather than a
 * new diff format: a field is "changed" exactly when its
 * [LwwRegister.timestamp] moved forward.
 */
public object DeltaGenerator {
    /**
     * Fields in [current] whose timestamp is strictly newer than the same
     * field in [baseline] — the minimal set to transmit so a remote
     * replica at [baseline] converges to [current]. A field present in
     * [current] but absent from [baseline] always counts as changed.
     */
    public fun generateDelta(
        baseline: Map<String, LwwRegister<Any?>>,
        current: Map<String, LwwRegister<Any?>>,
    ): Map<String, LwwRegister<Any?>> =
        current.filter { (key, value) ->
            val base = baseline[key]
            base == null || value.timestamp > base.timestamp
        }

    /** Whether applying [generateDelta] would produce anything at all — a cheap "is there work to send" check. */
    public fun hasChanges(
        baseline: Map<String, LwwRegister<Any?>>,
        current: Map<String, LwwRegister<Any?>>,
    ): Boolean = generateDelta(baseline, current).isNotEmpty()
}
