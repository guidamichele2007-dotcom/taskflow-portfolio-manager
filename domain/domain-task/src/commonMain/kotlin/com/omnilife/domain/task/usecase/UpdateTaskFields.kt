package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.OmniResult
import com.omnilife.domain.task.RecurrenceRule
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskPriority
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * MFC-R-06: every field is independently editable and autosaves — no
 * "save" step. Each parameter is an [Edit] wrapper so "leave unchanged" and
 * "set to null" are distinguishable (e.g. clearing a due date).
 */
public sealed interface Edit<out T> {
    public data object Unchanged : Edit<Nothing>
    public data class Set<T>(val value: T?) : Edit<T>
}

public class UpdateTaskFields(private val repository: TaskRepository, private val clock: Clock = Clock.System) {
    public suspend operator fun invoke(
        taskId: EntityId,
        title: Edit<String> = Edit.Unchanged,
        dueDate: Edit<LocalDate> = Edit.Unchanged,
        dueTime: Edit<LocalTime> = Edit.Unchanged,
        priority: Edit<TaskPriority> = Edit.Unchanged,
        recurrenceRule: Edit<RecurrenceRule> = Edit.Unchanged,
        notes: Edit<String> = Edit.Unchanged,
    ): OmniResult<Task> {
        val task = repository.findTaskById(taskId) ?: return OmniResult.Failure(TaskError.TaskNotFound(taskId))

        val newTitle = (title as? Edit.Set)?.value ?: task.title
        if (newTitle.isNullOrBlank()) return OmniResult.Failure(TaskError.MissingTitle)

        val updated = task.copy(
            title = newTitle,
            dueDate = if (dueDate is Edit.Set) dueDate.value else task.dueDate,
            dueTime = if (dueTime is Edit.Set) dueTime.value else task.dueTime,
            priority = (priority as? Edit.Set)?.value ?: task.priority,
            recurrenceRule = if (recurrenceRule is Edit.Set) recurrenceRule.value else task.recurrenceRule,
            notes = if (notes is Edit.Set) notes.value else task.notes,
            envelope = task.envelope.copy(modifiedAt = clock.now()),
        )
        repository.updateTask(updated)
        return OmniResult.Success(updated)
    }
}
