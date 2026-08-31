package com.omnilife.core.notifications

import com.omnilife.core.eventbus.EventBus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

/** What [NotificationBroker.request] decided to do with a request, right now. */
public enum class NotificationDisposition {
    SCHEDULED_IMMEDIATELY,
    ROUTED_TO_DIGEST,
    DEFERRED_FOR_QUIET_HOURS,
    SUPPRESSED_CATEGORY_DISABLED,
}

/**
 * NTF-001: "Broker centrale delle notifiche: ogni modulo *richiede* una notifica al broker; il
 * broker applica budget, raggruppamento, silenzi e priorità. Nessuna notifica diretta dai
 * moduli." This is that single entry point — every other component in this module (budget,
 * digest, quiet hours, categories, history, delivery) is orchestrated from here, never called
 * directly by a domain module.
 *
 * NTF §2 edge case ("50 richieste in un'ora → 1 digest 'mentre eri via'", TDR-30): once
 * [burstThreshold] requests have arrived within the trailing hour, further requests in that
 * burst are routed to the digest exactly like a budget overflow — the same mechanism serves both
 * triggers. This routes the *overflow* requests to the digest (the ones arriving after the
 * threshold trips), not the already-processed earlier ones — a deliberate simplification over
 * "collapse the whole burst retroactively," recorded as TDR-30.
 */
public class NotificationBroker(
    private val categoryRegistry: NotificationCategoryRegistry,
    private val historyStore: NotificationHistoryStore,
    private val budget: NotificationBudget,
    private val digest: NotificationDigest,
    private val localNotificationService: LocalNotificationService,
    private val eventBus: EventBus,
    private val quietHoursWindow: QuietHoursWindow = QuietHoursWindow(),
    private val digestDeliveryHour: Int = DEFAULT_DIGEST_DELIVERY_HOUR,
    private val burstThreshold: Int = DEFAULT_BURST_THRESHOLD,
) {
    private val deferredForQuietHours = mutableMapOf<String, NotificationRequest>()

    // ArrayDeque, not a plain List + removeAll: requests arrive in non-decreasing `now` order in
    // real use, so stale entries are always at the front — evicting from the front is O(1)
    // amortized per call. A full-list scan-and-filter here would be the same O(n^2) trap already
    // fixed once this sprint in core-sync's ORSet (see sprint3_report.md).
    private val recentRequestTimestamps = ArrayDeque<Instant>()
    private var lastDigestFlushDate: kotlinx.datetime.LocalDate? = null

    public fun request(
        request: NotificationRequest,
        now: Instant,
        zone: TimeZone,
    ): NotificationDisposition {
        eventBus.publish(NotificationEvent.NtfRequested(request, now))

        if (!categoryRegistry.isEnabled(request.category.id)) {
            val suppressed =
                request.copy(state = NotificationState.IGNORATA, outcome = NotificationOutcome.IGNORATA)
            historyStore.record(suppressed)
            return NotificationDisposition.SUPPRESSED_CATEGORY_DISABLED
        }

        if (QuietHours.isQuietAt(now, zone, quietHoursWindow)) {
            deferredForQuietHours[request.id] = request
            historyStore.record(request.copy(state = NotificationState.PIANIFICATA))
            return NotificationDisposition.DEFERRED_FOR_QUIET_HOURS
        }

        return evaluateAndDeliver(request, now, zone)
    }

    /**
     * NTF-004/NTF-AC-03: call on wake (background delivery); resolves every quiet-hours-deferred
     * request. Deliberately re-enters [evaluateAndDeliver] (the same budget/burst pipeline
     * [request] uses) rather than delivering directly — a wake-up after a long quiet period is
     * exactly the scenario the burst rule (NTF §2) exists for, so a woken request must not bypass
     * it.
     */
    public fun processDeferred(
        now: Instant,
        zone: TimeZone,
    ) {
        if (QuietHours.isQuietAt(now, zone, quietHoursWindow)) return
        val due = deferredForQuietHours.values.toList()
        for (deferredRequest in due) {
            deferredForQuietHours.remove(deferredRequest.id)
            when (SmartRescheduler.decide(deferredRequest, now)) {
                SmartRescheduleDecision.SHOW_AT_WAKE -> evaluateAndDeliver(deferredRequest, now, zone)
                SmartRescheduleDecision.EXPIRED -> {
                    val expired = deferredRequest.copy(state = NotificationState.SCADUTA_DI_SIGNIFICATO)
                    historyStore.record(expired)
                }
            }
        }
    }

    /** Budget/burst decision + delivery, shared by a fresh [request] and a woken [processDeferred] item. */
    private fun evaluateAndDeliver(
        request: NotificationRequest,
        now: Instant,
        zone: TimeZone,
    ): NotificationDisposition {
        if (isBurstConditionActive(now) || !budget.hasRoom(request.priority, now, zone)) {
            digest.addToDigest(request)
            historyStore.record(request.copy(outcome = NotificationOutcome.ASSORBITA_IN_DIGEST))
            categoryRegistry.recordOutcome(request.category.id, NotificationOutcome.ASSORBITA_IN_DIGEST)
            return NotificationDisposition.ROUTED_TO_DIGEST
        }

        budget.consume(request.priority, now, zone)
        showNow(request)
        return NotificationDisposition.SCHEDULED_IMMEDIATELY
    }

    /** Delivers the accumulated digest once [digestDeliveryHour] has passed for the local day, at most once per day. */
    public fun flushDigestIfDue(
        now: Instant,
        zone: TimeZone,
    ) {
        val local = now.toLocalDateTime(zone)
        if (local.hour < digestDeliveryHour) return
        if (lastDigestFlushDate == local.date) return
        val items = digest.flush()
        lastDigestFlushDate = local.date
        if (items.isEmpty()) return
        val summary = buildDigestSummaryRequest(items, now)
        showNow(summary)
    }

    /**
     * Records the terminal outcome of a shown request (mostrata/azionata/ignorata) — feeds
     * NTF-006 via [categoryRegistry].
     */
    public fun recordOutcome(
        request: NotificationRequest,
        outcome: NotificationOutcome,
    ) {
        val state =
            when (outcome) {
                NotificationOutcome.MOSTRATA -> NotificationState.MOSTRATA
                NotificationOutcome.AZIONATA -> NotificationState.AZIONATA
                NotificationOutcome.IGNORATA -> NotificationState.IGNORATA
                NotificationOutcome.ASSORBITA_IN_DIGEST -> NotificationState.MOSTRATA
            }
        historyStore.record(request.copy(state = state, outcome = outcome))
        categoryRegistry.recordOutcome(request.category.id, outcome)
    }

    /**
     * TDR-39: cancels a still-pending request — the only path a module has to stop a notification
     * it previously requested (NTF-001: never a direct call to [LocalNotificationService], always
     * through this broker). A no-op if [requestId] already fired, was never scheduled, or is
     * unknown — cancellation is best-effort, never an error a caller must handle.
     */
    public fun cancel(requestId: String) {
        deferredForQuietHours.remove(requestId)
        localNotificationService.cancel(requestId)
    }

    private fun showNow(request: NotificationRequest) {
        val channelSpec = channelSpecFor(request.category, request.priority)
        localNotificationService.show(request, channelSpec) { delivered -> historyStore.record(delivered) }
        historyStore.record(request.copy(state = NotificationState.PIANIFICATA))
    }

    private fun isBurstConditionActive(now: Instant): Boolean {
        recentRequestTimestamps.addLast(now)
        while (recentRequestTimestamps.isNotEmpty() && now - recentRequestTimestamps.first() > 1.hours) {
            recentRequestTimestamps.removeFirst()
        }
        return recentRequestTimestamps.size > burstThreshold
    }

    private fun channelSpecFor(
        category: NotificationCategory,
        priority: NotificationPriority,
    ): NotificationChannelSpec {
        val importance =
            when (priority) {
                NotificationPriority.PROMEMORIA_UTENTE -> NotificationImportance.HIGH
                NotificationPriority.UTILE -> NotificationImportance.DEFAULT
                NotificationPriority.INFORMATIVA -> NotificationImportance.LOW
            }
        return NotificationChannelSpec(
            channelId = category.id,
            displayName = category.moduleName,
            importance = importance,
        )
    }

    private fun buildDigestSummaryRequest(
        items: List<NotificationRequest>,
        now: Instant,
    ): NotificationRequest =
        NotificationRequest(
            id = "digest-${now.toEpochMilliseconds()}",
            category = DIGEST_CATEGORY,
            priority = NotificationPriority.INFORMATIVA,
            entityReference = EntityReference(entityId = "digest", entityType = "system.digest"),
            title = "Riepilogo",
            body = "${items.size} notifiche in attesa",
            scheduledFor = now,
        )

    public companion object {
        public const val DEFAULT_DIGEST_DELIVERY_HOUR: Int = 18
        public const val DEFAULT_BURST_THRESHOLD: Int = 50
        public val DIGEST_CATEGORY: NotificationCategory =
            NotificationCategory(id = "system.digest", moduleName = "system")
    }
}
