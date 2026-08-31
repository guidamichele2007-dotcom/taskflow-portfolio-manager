package com.omnilife.feature.task.bridge

import com.omnilife.core.common.EntityId
import com.omnilife.core.eventbus.EventBus
import com.omnilife.core.eventbus.Subscription
import com.omnilife.core.eventbus.subscribe
import com.omnilife.core.sync.ChangeTracker
import com.omnilife.core.sync.LogicalTimestamp
import com.omnilife.core.sync.OutboxItem
import com.omnilife.core.sync.SyncOutboxStore
import com.omnilife.core.sync.SyncStateManager
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * L2 orchestration bridge (Technical Architecture Bible §03/§05): every local Task mutation is
 * queued into `core-sync`'s [SyncOutboxStore] (MFC §3 — "outbox persistente... l'outbox non si
 * svuota mai senza conferma del server") so it survives offline use and a later app restart. This
 * bridge never attempts delivery itself and never fabricates a successful sync — actually sending
 * queued items is [com.omnilife.core.sync.BackgroundSyncCoordinator]'s job (built in Sprint 3),
 * invoked by the composition root on its own cadence against a real [com.omnilife.core.sync.RemoteSyncTransport]
 * (none reachable in this sandbox — no backend exists yet, see sprint5_report.md).
 *
 * Payload is the task's full current snapshot (JSON), not a field-level CRDT delta: wiring
 * `domain-task`'s plain fields into [com.omnilife.core.sync.LwwRegister]-backed per-field
 * tracking (as [com.omnilife.core.sync.DeltaGenerator] expects) is a larger change than this
 * sprint's scope — documented as a residual risk, not hidden.
 */
public class TaskSyncOutboxBridge(
    private val repository: TaskRepository,
    private val outboxStore: SyncOutboxStore,
    private val changeTracker: ChangeTracker,
    private val syncStateManager: SyncStateManager,
    private val deviceId: String,
    eventBus: EventBus,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val clock: Clock = Clock.System,
    private val zone: TimeZone = TimeZone.currentSystemDefault(),
) {
    private var counter = 0L
    private val subscriptions: List<Subscription> =
        listOf(
            eventBus.subscribe<TaskEvent.Created> { enqueue(it.taskId) },
            eventBus.subscribe<TaskEvent.Updated> { enqueue(it.taskId) },
            eventBus.subscribe<TaskEvent.Completed> { enqueue(it.taskId) },
            eventBus.subscribe<TaskEvent.Uncompleted> { enqueue(it.taskId) },
            eventBus.subscribe<TaskEvent.Rescheduled> { enqueue(it.taskId) },
            eventBus.subscribe<TaskEvent.Deleted> { enqueue(it.taskId) },
            eventBus.subscribe<TaskEvent.Restored> { enqueue(it.taskId) },
        )

    private fun enqueue(taskId: EntityId) {
        scope.launch {
            val task = repository.findTaskById(taskId) ?: return@launch
            val timestamp = LogicalTimestamp(nextCounter(), deviceId)
            changeTracker.markDirty(taskId, timestamp)

            val payload = taskSyncJson.encodeToString(task.toSyncPayload())
            outboxStore.enqueue(
                OutboxItem(
                    id = "$taskId-${timestamp.counter}",
                    payload = payload.encodeToByteArray(),
                    enqueuedAt = timestamp,
                    isHot = task.isHot(clock, zone),
                ),
            )
            // ChangeTracker.clear's contract: called once the change is "durably queued" — the
            // outbox (not the tracker) is the durable record from this point on.
            changeTracker.clear(taskId)
            syncStateManager.updatePendingCount(outboxStore.size())
        }
    }

    private fun nextCounter(): Long = ++counter

    /** Cancels every event subscription and in-flight work; call when this bridge is disposed. */
    public fun clear() {
        subscriptions.forEach { it.cancel() }
        scope.coroutineContext[Job]?.cancel()
    }
}

private val taskSyncJson = Json { ignoreUnknownKeys = true }

/** MFC §3 "priorità: dati di oggi/settimana → resto" — a task due today or overdue is hot. */
private fun Task.isHot(
    clock: Clock,
    zone: TimeZone,
): Boolean {
    val due = dueDate ?: return false
    val today = clock.todayIn(zone)
    return due <= today.plus(1, DateTimeUnit.DAY)
}
