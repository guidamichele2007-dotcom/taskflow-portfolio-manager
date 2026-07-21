package com.omnilife.domain.task.persistence

import app.cash.sqldelight.db.SqlDriver
import com.omnilife.core.common.EntityId
import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.TaskList
import com.omnilife.domain.task.TaskRepository
import com.omnilife.domain.task.TaskSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * SQLDelight-backed [TaskRepository] (TDR-20). Sorting/filtering beyond a
 * single indexed column is applied in memory after a broader SQL fetch —
 * acceptable at this sprint's scope (MFC-AC-07's 50,000-entity/100ms budget
 * is a product-wide target for the eventual full implementation, not
 * something this bootstrap-adjacent slice claims to already satisfy).
 */
public class SqlDelightTaskRepository(driver: SqlDriver) : TaskRepository {
    private val database = TaskDatabase(driver)
    private val queries = database.taskQueries

    override suspend fun insertTask(task: Task): Unit =
        withContext(Dispatchers.Default) {
            val row = task.toRow()
            queries.insertOrReplaceTask(
                row.id,
                row.ownerAccountId,
                row.schemaVersion,
                row.createdAt,
                row.createdByDevice,
                row.modifiedAt,
                row.modifiedByDevice,
                row.lifecycleState,
                row.trashedAt,
                row.title,
                row.dueDate,
                row.dueTime,
                row.priority,
                row.recurrenceRuleJson,
                row.listId,
                row.notes,
                row.completed,
                row.completedAt,
                row.manualOrder,
                row.reminderConfigJson,
            )
        }

    override suspend fun updateTask(task: Task): Unit = insertTask(task)

    override suspend fun findTaskById(id: EntityId): Task? =
        withContext(Dispatchers.Default) {
            queries.selectTaskById(id).executeAsOneOrNull()?.toDomain()
        }

    override suspend fun findTasks(
        filter: TaskFilter,
        sort: TaskSort,
    ): List<Task> =
        withContext(Dispatchers.Default) {
            val rows =
                when {
                    filter.listId != null ->
                        queries.selectTasksByLifecycleAndList(filter.lifecycleState.name, filter.listId)
                            .executeAsList()

                    filter.priority != null ->
                        queries
                            .selectTasksByLifecycleAndPriority(filter.lifecycleState.name, filter.priority.name)
                            .executeAsList()

                    else -> queries.selectTasksByLifecycle(filter.lifecycleState.name).executeAsList()
                }
            rows.map { it.toDomain() }
                .filter { task ->
                    (filter.includeCompleted || !task.completed) &&
                        (filter.dueBefore == null || (task.dueDate != null && task.dueDate < filter.dueBefore)) &&
                        (filter.dueAfter == null || (task.dueDate != null && task.dueDate > filter.dueAfter))
                }
                .let { applySort(it, sort) }
        }

    override suspend fun searchTasks(
        query: String,
        lifecycleState: EntityLifecycleState,
    ): List<Task> =
        withContext(Dispatchers.Default) {
            queries.searchTasksByLifecycle(lifecycleState.name, query).executeAsList().map { it.toDomain() }
        }

    override suspend fun permanentlyDeleteTask(id: EntityId): Unit =
        withContext(Dispatchers.Default) {
            queries.deleteTaskPermanently(id)
        }

    override suspend fun findSubtasks(taskId: EntityId): List<Subtask> =
        withContext(Dispatchers.Default) {
            queries.selectSubtasksByTaskId(taskId).executeAsList().map { it.toDomain() }
        }

    override suspend fun insertSubtask(subtask: Subtask): Unit =
        withContext(Dispatchers.Default) {
            val row = subtask.toRow()
            queries.insertOrReplaceSubtask(row.id, row.taskId, row.title, row.completed, row.orderIndex)
        }

    override suspend fun updateSubtask(subtask: Subtask): Unit = insertSubtask(subtask)

    override suspend fun deleteSubtask(id: EntityId): Unit =
        withContext(Dispatchers.Default) {
            queries.deleteSubtaskById(id)
        }

    override suspend fun permanentlyDeleteSubtasksForTask(taskId: EntityId): Unit =
        withContext(Dispatchers.Default) {
            queries.deleteSubtasksByTaskId(taskId)
        }

    override suspend fun findListById(id: EntityId): TaskList? =
        withContext(Dispatchers.Default) {
            queries.selectTaskListById(id).executeAsOneOrNull()?.toDomain()
        }

    override suspend fun findAllLists(): List<TaskList> =
        withContext(Dispatchers.Default) {
            queries.selectAllTaskLists().executeAsList().map { it.toDomain() }
        }

    override suspend fun insertList(list: TaskList): Unit =
        withContext(Dispatchers.Default) {
            val row = list.toRow()
            queries.insertOrReplaceTaskList(
                row.id,
                row.ownerAccountId,
                row.schemaVersion,
                row.createdAt,
                row.createdByDevice,
                row.modifiedAt,
                row.modifiedByDevice,
                row.lifecycleState,
                row.trashedAt,
                row.name,
                row.area,
                row.isDefault,
                row.manualOrder,
            )
        }

    override suspend fun updateList(list: TaskList): Unit = insertList(list)

    private fun applySort(
        tasks: List<Task>,
        sort: TaskSort,
    ): List<Task> =
        when (sort) {
            TaskSort.DEFAULT ->
                tasks.sortedWith(
                    compareBy<Task> { it.manualOrder == null }
                        .thenBy { it.manualOrder }
                        .thenBy { it.dueDate == null }
                        .thenBy { it.dueDate }
                        .thenByDescending { it.priority.ordinal },
                )

            TaskSort.DUE_DATE -> tasks.sortedWith(compareBy({ it.dueDate == null }, { it.dueDate }))
            TaskSort.PRIORITY -> tasks.sortedByDescending { it.priority.ordinal }
            TaskSort.CREATED_AT -> tasks.sortedBy { it.envelope.createdAt }
        }
}
