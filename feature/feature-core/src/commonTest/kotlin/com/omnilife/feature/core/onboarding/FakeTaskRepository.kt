package com.omnilife.feature.core.onboarding

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.TaskList
import com.omnilife.domain.task.TaskRepository
import com.omnilife.domain.task.TaskSort
import com.omnilife.domain.task.sortedByTaskSort

/** In-memory [TaskRepository] for ViewModel tests — mirrors `feature-task`'s own test fake. */
internal class FakeTaskRepository : TaskRepository {
    val tasks: MutableMap<EntityId, Task> = mutableMapOf()
    val subtasks: MutableMap<EntityId, Subtask> = mutableMapOf()
    val lists: MutableMap<EntityId, TaskList> = mutableMapOf()

    override suspend fun insertTask(task: Task) {
        tasks[task.envelope.id] = task
    }

    override suspend fun updateTask(task: Task) {
        tasks[task.envelope.id] = task
    }

    override suspend fun findTaskById(id: EntityId): Task? = tasks[id]

    override suspend fun findTasks(
        filter: TaskFilter,
        sort: TaskSort,
    ): List<Task> =
        tasks.values.filter { task ->
            task.envelope.lifecycleState == filter.lifecycleState &&
                (filter.listId == null || task.listId == filter.listId) &&
                (filter.priority == null || task.priority == filter.priority) &&
                (filter.includeCompleted || !task.completed)
        }.sortedByTaskSort(sort)

    override suspend fun searchTasks(
        query: String,
        lifecycleState: EntityLifecycleState,
    ): List<Task> =
        tasks.values.filter {
            it.envelope.lifecycleState == lifecycleState &&
                (it.title.contains(query, ignoreCase = true) || it.notes?.contains(query, ignoreCase = true) == true)
        }

    override suspend fun permanentlyDeleteTask(id: EntityId) {
        tasks.remove(id)
    }

    override suspend fun findSubtasks(taskId: EntityId): List<Subtask> =
        subtasks.values.filter { it.taskId == taskId }.sortedBy { it.order }

    override suspend fun insertSubtask(subtask: Subtask) {
        subtasks[subtask.id] = subtask
    }

    override suspend fun updateSubtask(subtask: Subtask) {
        subtasks[subtask.id] = subtask
    }

    override suspend fun deleteSubtask(id: EntityId) {
        subtasks.remove(id)
    }

    override suspend fun permanentlyDeleteSubtasksForTask(taskId: EntityId) {
        subtasks.values.filter { it.taskId == taskId }.forEach { subtasks.remove(it.id) }
    }

    override suspend fun findListById(id: EntityId): TaskList? = lists[id]

    override suspend fun findAllLists(): List<TaskList> = lists.values.toList()

    override suspend fun insertList(list: TaskList) {
        lists[list.envelope.id] = list
    }

    override suspend fun updateList(list: TaskList) {
        lists[list.envelope.id] = list
    }
}
