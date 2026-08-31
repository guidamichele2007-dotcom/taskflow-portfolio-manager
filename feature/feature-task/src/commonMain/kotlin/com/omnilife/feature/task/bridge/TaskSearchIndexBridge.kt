package com.omnilife.feature.task.bridge

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.core.eventbus.EventBus
import com.omnilife.core.eventbus.Subscription
import com.omnilife.core.eventbus.subscribe
import com.omnilife.core.search.SearchIndexer
import com.omnilife.core.search.SimpleIndexableEntity
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * L2 orchestration bridge (Technical Architecture Bible §03): keeps `core-search`'s index
 * consistent with `domain-task`'s writes, entirely by subscribing to [TaskEvent] on the
 * [EventBus] — neither module depends on the other directly (SRCH-AC-02: "indice aggiornato...
 * mai risultati fantasma o mancanti dopo una modifica").
 *
 * A trashed task is **re-indexed**, never removed (SRCH-006: archived/trashed entities stay
 * indexed, excluded from default results only via [com.omnilife.core.search.SearchFilter]'s
 * opt-in) — [SearchIndexer.remove] is reserved for permanent deletion
 * ([TaskEvent.PermanentlyDeleted], Sprint 6 — previously unpublished, a documented Sprint 5
 * residual risk; see sprint6_report.md §6).
 */
public class TaskSearchIndexBridge(
    private val repository: TaskRepository,
    private val searchIndexer: SearchIndexer,
    eventBus: EventBus,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val subscriptions: List<Subscription> =
        listOf(
            eventBus.subscribe<TaskEvent.Created> { reindex(it.taskId) },
            eventBus.subscribe<TaskEvent.Updated> { reindex(it.taskId) },
            eventBus.subscribe<TaskEvent.Completed> { reindex(it.taskId) },
            eventBus.subscribe<TaskEvent.Uncompleted> { reindex(it.taskId) },
            eventBus.subscribe<TaskEvent.Rescheduled> { reindex(it.taskId) },
            eventBus.subscribe<TaskEvent.Deleted> { reindex(it.taskId) },
            eventBus.subscribe<TaskEvent.Restored> { reindex(it.taskId) },
            eventBus.subscribe<TaskEvent.PermanentlyDeleted> { searchIndexer.remove(it.taskId) },
        )

    private fun reindex(taskId: EntityId) {
        scope.launch {
            val task = repository.findTaskById(taskId) ?: return@launch
            searchIndexer.index(task.toIndexableEntity())
        }
    }

    /**
     * Full rebuild from every task currently in [repository] (SearchIndexer.rebuild's documented
     * recovery path). Call once at startup so tasks created in a prior app session — before this
     * bridge's subscriptions existed for them — are searchable immediately, not only after their
     * next mutation.
     */
    public suspend fun rebuildIndex() {
        // SRCH-006: trashed entities stay indexed (excluded from default results only via
        // SearchFilter's opt-in), so both lifecycle states must be fetched separately — the
        // default TaskFilter() only returns ACTIVE.
        val active = repository.findTasks(TaskFilter(lifecycleState = EntityLifecycleState.ACTIVE))
        val trashed = repository.findTasks(TaskFilter(lifecycleState = EntityLifecycleState.TRASHED))
        searchIndexer.rebuild((active + trashed).map { it.toIndexableEntity() })
    }

    /** Cancels every event subscription and in-flight indexing work; call when this bridge is disposed. */
    public fun clear() {
        subscriptions.forEach { it.cancel() }
        scope.coroutineContext[Job]?.cancel()
    }
}

private fun Task.toIndexableEntity(): SimpleIndexableEntity =
    SimpleIndexableEntity(
        id = envelope.id,
        entityType = "task",
        title = title,
        content = notes,
        category = null,
        lifecycleState = envelope.lifecycleState.name,
        createdAt = envelope.createdAt,
        modifiedAt = envelope.modifiedAt,
    )
