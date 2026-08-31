package com.omnilife.feature.task.bridge

import com.omnilife.core.common.EntityId
import com.omnilife.core.eventbus.EventBus
import com.omnilife.core.eventbus.Subscription
import com.omnilife.core.eventbus.subscribe
import com.omnilife.core.notifications.DeepLinkResolver
import com.omnilife.core.notifications.EntityReference
import com.omnilife.core.notifications.NotificationBroker
import com.omnilife.core.notifications.NotificationCategory
import com.omnilife.core.notifications.NotificationPriority
import com.omnilife.core.notifications.NotificationRequest
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * L2 orchestration bridge (Technical Architecture Bible §03): connects `domain-task`'s
 * [com.omnilife.domain.task.ReminderConfig] to `core-notifications`'s [NotificationBroker] —
 * "Task → Notification scheduling → Notification history → Action/deep link → opening the
 * relevant screen" (Macro Sprint 5 requirement). Never calls a platform notification API
 * directly (NTF-001) — every request/cancel goes through the broker.
 *
 * A task carries at most one active reminder at a time (TASK-003: [ReminderConfig] is a single
 * field, not a list), so the task's own id doubles as the notification's stable [NotificationRequest.id]
 * — this makes reschedule-on-edit a plain cancel-then-request, never a leaked duplicate.
 *
 * **Verified in this sandbox**: the request/cancel/reschedule logic itself, against
 * [NotificationBroker] (Sprint 3, fully unit-tested) — see `TaskNotificationBridgeTest`.
 * **Not verified in this sandbox**: whether a notification actually appears on a real Android/iOS
 * device (no SDK/Xcode host here — see README-BUILD.md §4); `LocalNotificationService`'s platform
 * `actual`s are unit-tested only on the JVM target, same limitation already documented for
 * `core-notifications` since Sprint 3.
 */
public class TaskNotificationBridge(
    private val repository: TaskRepository,
    private val notificationBroker: NotificationBroker,
    eventBus: EventBus,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val clock: Clock = Clock.System,
    private val zone: TimeZone = TimeZone.currentSystemDefault(),
) {
    private val subscriptions: List<Subscription> =
        listOf(
            eventBus.subscribe<TaskEvent.Created> { reconcile(it.taskId) },
            eventBus.subscribe<TaskEvent.Updated> { reconcile(it.taskId) },
            eventBus.subscribe<TaskEvent.Rescheduled> { reconcile(it.taskId) },
            eventBus.subscribe<TaskEvent.Uncompleted> { reconcile(it.taskId) },
            eventBus.subscribe<TaskEvent.Restored> { reconcile(it.taskId) },
            eventBus.subscribe<TaskEvent.Completed> { cancelReminder(it.taskId) },
            eventBus.subscribe<TaskEvent.Deleted> { cancelReminder(it.taskId) },
        )

    /** Fire-and-forget wrapper around [reconcileSuspend] for the event-subscription path. */
    private fun reconcile(taskId: EntityId) {
        scope.launch { reconcileSuspend(taskId) }
    }

    /**
     * Re-derives whether [taskId] should have a scheduled reminder right now, and makes the
     * broker's state match — always cancels first (idempotent no-op if nothing was scheduled),
     * then re-requests only if every precondition still holds (TDR-39).
     */
    private suspend fun reconcileSuspend(taskId: EntityId) {
        notificationBroker.cancel(taskId)
        val task = repository.findTaskById(taskId) ?: return
        if (task.completed) return
        val reminderConfig = task.reminderConfig ?: return
        val dueDate = task.dueDate ?: return
        val dueTime = task.dueTime ?: return

        val dueInstant = LocalDateTime(dueDate, dueTime).toInstant(zone)
        val leadMillis = reminderConfig.leadMinutesBeforeDue * MILLIS_PER_MINUTE
        val scheduledFor = Instant.fromEpochMilliseconds(dueInstant.toEpochMilliseconds() - leadMillis)
        val reference = EntityReference(taskId, TASK_ENTITY_TYPE)

        notificationBroker.request(
            NotificationRequest(
                id = taskId,
                category = TASK_REMINDER_CATEGORY,
                priority = NotificationPriority.PROMEMORIA_UTENTE,
                entityReference = reference,
                title = task.title,
                body = REMINDER_BODY,
                scheduledFor = scheduledFor,
                deepLink = DeepLinkResolver.buildDeepLink(reference),
            ),
            now = clock.now(),
            zone = zone,
        )
    }

    /**
     * MVP Release 1.0: `AlarmManager` clears every pending alarm on device reboot — Android's own
     * documented behavior, not a bug in this app — so every scheduled reminder would silently
     * vanish the moment the user restarts their phone unless something re-derives and
     * re-schedules them. Meant to be called once, at boot, for every still-active task (an
     * app-shell `BroadcastReceiver` for `ACTION_BOOT_COMPLETED` is the real caller — out of this
     * module, which has no platform/manifest knowledge).
     */
    public suspend fun reconcileAll() {
        repository.findTasks(TaskFilter()).forEach { reconcileSuspend(it.envelope.id) }
    }

    private fun cancelReminder(taskId: EntityId) {
        notificationBroker.cancel(taskId)
    }

    /** Cancels every event subscription and in-flight work; call when this bridge is disposed. */
    public fun clear() {
        subscriptions.forEach { it.cancel() }
        scope.coroutineContext[Job]?.cancel()
    }

    public companion object {
        public const val TASK_ENTITY_TYPE: String = "task"
        public const val REMINDER_BODY: String = "Promemoria"
        private const val MILLIS_PER_MINUTE: Long = 60_000L
        public val TASK_REMINDER_CATEGORY: NotificationCategory =
            NotificationCategory(id = "task.reminder", moduleName = "task")
    }
}
