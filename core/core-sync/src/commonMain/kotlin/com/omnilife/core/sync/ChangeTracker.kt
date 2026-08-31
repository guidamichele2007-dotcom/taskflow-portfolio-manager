package com.omnilife.core.sync

/**
 * Local dirty-set (Technical Architecture Bible §05 — change tracking:
 * *what* changed locally since the last successful sync, independent of
 * [SyncOutboxStore] which holds serialized payloads already queued to
 * send). An entity can be marked dirty many times between two syncs;
 * tracking collapses to "changed since [clear]", never a change counter.
 */
public interface ChangeTracker {
    public fun markDirty(
        entityId: String,
        at: LogicalTimestamp,
    )

    public fun isDirty(entityId: String): Boolean

    public fun dirtyEntityIds(): Set<String>

    /** Called once an entity's change has been durably queued (moved into the outbox). */
    public fun clear(entityId: String)

    public fun clearAll()
}

public class InMemoryChangeTracker : ChangeTracker {
    private val dirtySince = mutableMapOf<String, LogicalTimestamp>()

    override fun markDirty(
        entityId: String,
        at: LogicalTimestamp,
    ) {
        val existing = dirtySince[entityId]
        if (existing == null || at > existing) {
            dirtySince[entityId] = at
        }
    }

    override fun isDirty(entityId: String): Boolean = dirtySince.containsKey(entityId)

    override fun dirtyEntityIds(): Set<String> = dirtySince.keys.toSet()

    override fun clear(entityId: String) {
        dirtySince.remove(entityId)
    }

    override fun clearAll() {
        dirtySince.clear()
    }
}
