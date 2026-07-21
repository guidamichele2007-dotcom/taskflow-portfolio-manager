package com.omnilife.domain.task

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.EntityLifecycleState

/**
 * Persistence port for the Task module (Technical Architecture Bible §01 §4:
 * L3 depends only on abstractions it declares). The concrete implementation
 * (SQLDelight-backed, TDR-20) lives in this same module in this sprint — see
 * README-BUILD.md §11 for the convention and when it should move to its own
 * module.
 *
 * Local-first by construction (Technical Architecture Bible §05 §1): every
 * method here is a local operation; synchronization (if any) observes this
 * same write path from the outside and is never invoked by it directly.
 */
public interface TaskRepository {
    public suspend fun insertTask(task: Task)

    public suspend fun updateTask(task: Task)

    public suspend fun findTaskById(id: EntityId): Task?

    public suspend fun findTasks(filter: TaskFilter = TaskFilter(), sort: TaskSort = TaskSort.DEFAULT): List<Task>

    /**
     * Local text search over title/notes (TASK-R-05 scope: within this
     * module, not the cross-module SRCH-* feature).
     */
    public suspend fun searchTasks(
        query: String,
        lifecycleState: EntityLifecycleState = EntityLifecycleState.ACTIVE,
    ): List<Task>

    public suspend fun permanentlyDeleteTask(id: EntityId)

    public suspend fun findSubtasks(taskId: EntityId): List<Subtask>

    public suspend fun insertSubtask(subtask: Subtask)

    public suspend fun updateSubtask(subtask: Subtask)

    public suspend fun deleteSubtask(id: EntityId)

    public suspend fun permanentlyDeleteSubtasksForTask(taskId: EntityId)

    public suspend fun findListById(id: EntityId): TaskList?

    public suspend fun findAllLists(): List<TaskList>

    public suspend fun insertList(list: TaskList)

    public suspend fun updateList(list: TaskList)
}
