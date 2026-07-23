package com.omnilife.core.notifications

/**
 * NTF-007: "pannello con le notifiche recenti e i controlli per categoria" — the in-app
 * center's data source. Every [NotificationRequest] transition is recorded here regardless of
 * outcome (DM-NTF-01: "esito... aggiornato nel tempo"), so a permission-denied device (NTF §2
 * edge case) still shows full history in-app even though nothing was ever pushed.
 */
public interface NotificationHistoryStore {
    public fun record(request: NotificationRequest)

    /** Most recent first, capped at [limit]. */
    public fun recent(limit: Int = DEFAULT_RECENT_LIMIT): List<NotificationRequest>

    public fun byCategory(categoryId: String): List<NotificationRequest>

    public fun findById(requestId: String): NotificationRequest?

    public companion object {
        public const val DEFAULT_RECENT_LIMIT: Int = 50
    }
}

public class InMemoryNotificationHistoryStore : NotificationHistoryStore {
    // Insertion order = recency order for this map's purposes: a re-recorded id is moved to the
    // end (LinkedHashMap.put on an existing key keeps original position, so remove-then-put).
    private val requestsById = LinkedHashMap<String, NotificationRequest>()

    override fun record(request: NotificationRequest) {
        requestsById.remove(request.id)
        requestsById[request.id] = request
    }

    override fun recent(limit: Int): List<NotificationRequest> =
        requestsById.values.toList().takeLast(limit).asReversed()

    override fun byCategory(categoryId: String): List<NotificationRequest> =
        requestsById.values.filter { it.category.id == categoryId }

    override fun findById(requestId: String): NotificationRequest? = requestsById[requestId]
}
