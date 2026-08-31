package com.omnilife.core.notifications

/**
 * NTF-003: non-urgent notifications (recurrence completions, budget overflow — NTF-AC-01)
 * accumulate here instead of firing individually, to be delivered as one digest at a chosen
 * time. Also backs the NTF §2 edge case ("50 richieste in un'ora → 1 digest 'mentre eri via'"):
 * [NotificationBroker] routes burst-volume requests here too, the same accumulate-then-[flush]
 * mechanism serving both triggers.
 */
public interface NotificationDigest {
    public fun addToDigest(request: NotificationRequest)

    public fun pendingCount(): Int

    /** Empties the digest, returning everything accumulated — call when its scheduled delivery time arrives. */
    public fun flush(): List<NotificationRequest>
}

public class InMemoryNotificationDigest : NotificationDigest {
    private val pending = mutableListOf<NotificationRequest>()

    override fun addToDigest(request: NotificationRequest) {
        pending += request
    }

    override fun pendingCount(): Int = pending.size

    override fun flush(): List<NotificationRequest> {
        val result = pending.toList()
        pending.clear()
        return result
    }
}
