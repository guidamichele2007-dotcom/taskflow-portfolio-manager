package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.TaskPriority
import com.omnilife.domain.task.TaskRepository
import com.omnilife.domain.task.TaskSort
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** TASK-012: exactly 3 fixed views, no custom views — plus TASK-014's dedicated overdue grouping. */
public enum class TaskListMode {
    TODAY,
    UPCOMING,
    ALL,
    OVERDUE,
}

/**
 * IA-030/031/032/034: the four Task list surfaces. TASK-014 requires overdue
 * items are grouped separately and never mixed with today's — enforced here
 * by [TaskListMode.TODAY] excluding overdue items outright.
 */
public class GetTasksForView(
    private val repository: TaskRepository,
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    public suspend operator fun invoke(
        mode: TaskListMode,
        listId: EntityId? = null,
        priority: TaskPriority? = null,
    ): List<Task> {
        val today = clock.todayIn(timeZone)
        val all = repository.findTasks(TaskFilter(listId = listId, priority = priority), sort = TaskSort.DEFAULT)
        return when (mode) {
            TaskListMode.TODAY -> all.filter { !it.isOverdue(today) && (it.dueDate == null || it.dueDate == today) }
            TaskListMode.UPCOMING -> all.filter { it.dueDate != null && it.dueDate > today }
            TaskListMode.ALL -> all
            TaskListMode.OVERDUE -> all.filter { it.isOverdue(today) }
        }
    }
}
