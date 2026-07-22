package com.omnilife.core.sync

/**
 * The conflict resolver (MFC-R-08, Technical Architecture Bible §05 §3):
 * merges two versions of the same entity **field by field**, generically —
 * an entity is represented as a `Map<String, LwwRegister<Any?>>` rather
 * than a concrete domain type, so this module has no dependency on
 * `domain-task` or any other `domain-*` module (out of this sprint's scope
 * to wire up). A future domain entity adopts this by exposing its fields
 * as such a map and rebuilding itself from the merged map — not done here.
 *
 * Commutative and order-independent by construction: each field merges via
 * [LwwRegister.merge] (itself commutative/idempotent), and merging two maps
 * key-by-key preserves that property (verified in this module's tests) —
 * "l'ordine di arrivo non altera il risultato finale" (MFC §3), a property
 * the Bible requires be tested, not just asserted.
 */
public object EntityFieldMerger {
    public fun merge(
        a: Map<String, LwwRegister<Any?>>,
        b: Map<String, LwwRegister<Any?>>,
    ): Map<String, LwwRegister<Any?>> {
        val allKeys = a.keys + b.keys
        return allKeys.associateWith { key ->
            val fieldA = a[key]
            val fieldB = b[key]
            when {
                fieldA == null -> requireNotNull(fieldB)
                fieldB == null -> fieldA
                else -> LwwRegister.merge(fieldA, fieldB)
            }
        }
    }
}
