package com.omnilife.core.sync

/**
 * One queued mutation (Technical Architecture Bible §05 §2 — "outbox
 * persistente, priorità dati caldi"). [isHot] marks "today/this week" data
 * (MFC §3: "Priorità: dati di oggi/settimana → resto").
 */
public data class OutboxItem(
    public val id: String,
    public val payload: ByteArray,
    public val enqueuedAt: LogicalTimestamp,
    public val isHot: Boolean,
) {
    override fun equals(other: Any?): Boolean =
        other is OutboxItem && id == other.id && payload.contentEquals(other.payload) &&
            enqueuedAt == other.enqueuedAt && isHot == other.isHot

    override fun hashCode(): Int = id.hashCode()
}

/**
 * The offline queue (MFC §3): "nessun limite pratico di durata offline",
 * "l'outbox non si svuota mai senza conferma del server" — [acknowledge]
 * is the only way an item leaves the queue, never a timeout or a
 * best-effort drop.
 */
public interface SyncOutboxStore {
    public fun enqueue(item: OutboxItem)

    /**
     * Hot items first, then oldest-first among equally-hot items — never a
     * size/FIFO-only order. Does **not** remove the item — only
     * [acknowledge] does, so a failed sync attempt leaves it queued.
     */
    public fun peekNext(): OutboxItem?

    public fun acknowledge(id: String)

    public fun size(): Int

    public fun peekAll(): List<OutboxItem>
}

public class InMemorySyncOutboxStore : SyncOutboxStore {
    private val items = mutableMapOf<String, OutboxItem>()

    override fun enqueue(item: OutboxItem) {
        items[item.id] = item
    }

    override fun peekNext(): OutboxItem? =
        items.values.minWithOrNull(compareByDescending<OutboxItem> { it.isHot }.thenBy { it.enqueuedAt })

    override fun acknowledge(id: String) {
        items.remove(id)
    }

    override fun size(): Int = items.size

    override fun peekAll(): List<OutboxItem> = items.values.toList()
}
