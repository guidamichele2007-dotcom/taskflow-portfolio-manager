package com.omnilife.core.sync

/**
 * One entity's full mergeable state: scalar fields (LWW) plus named
 * relation sets (OR-Set) — the two CRDT shapes every module's entities
 * reduce to (Data Model Bible §11: "campo → LWW, insieme di riferimenti →
 * OR-Set").
 */
public data class MergeableEntitySnapshot(
    public val entityId: String,
    public val fields: Map<String, LwwRegister<Any?>>,
    public val relations: Map<String, ORSet<String>>,
)

/**
 * Orchestrates a full entity merge: per-field resolution via
 * [ConflictResolver], per-relation merge via [ORSet.merge] — the single
 * entry point a sync round calls once per entity, rather than every
 * caller hand-assembling field and relation merges itself.
 */
public class MergeEngine(private val conflictResolver: ConflictResolver = LwwConflictResolver()) {
    public fun merge(
        local: MergeableEntitySnapshot,
        remote: MergeableEntitySnapshot,
    ): MergeableEntitySnapshot {
        require(local.entityId == remote.entityId) {
            "Cannot merge snapshots of different entities: ${local.entityId} vs ${remote.entityId}"
        }

        val mergedFields =
            (local.fields.keys + remote.fields.keys).associateWith { key ->
                conflictResolver.resolveField(key, local.fields[key], remote.fields[key]).resolved
            }

        val mergedRelations =
            (local.relations.keys + remote.relations.keys).associateWith { key ->
                val localSet = local.relations[key] ?: ORSet.empty()
                val remoteSet = remote.relations[key] ?: ORSet.empty()
                localSet.merge(remoteSet)
            }

        return MergeableEntitySnapshot(local.entityId, mergedFields, mergedRelations)
    }
}
