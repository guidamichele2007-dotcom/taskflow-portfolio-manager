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
 * "save" step. Each field is an [Edit] wrapper so "leave unchanged" and
 * "set to null" are distinguishable (e.g. clearing a due date).
 */
public sealed interface Edit<out T> {
    public data object Unchanged : Edit<Nothing>

    public data class Set<T>(val value: T?) : Edit<T>
}

public data class TaskFieldEdits(
    val title: Edit<String> = Edit.Unchanged,
    val dueDate: Edit<LocalDate> = Edit.Unchanged,
    val dueTime: Edit<LocalTime> = Edit.Unchanged,
    val priority: Edit<TaskPriority> = Edit.Unchanged,
    val recurrenceRule: Edit<RecurrenceRule> = Edit.Unchanged,
    val notes: Edit<String> = Edit.Unchanged,
)

private fun <T> Edit<T>.resolve(current: T?): T? = if (this is Edit.Set) value else current

public class UpdateTaskFields(private val repository: TaskRepository, private val clock: Clock = Clock.System) {
    public suspend operator fun invoke(
        taskId: EntityId,
        edits: TaskFieldEdits = TaskFieldEdits(),
    ): OmniResult<Task> {
        val task = repository.findTaskById(taskId) ?: return OmniResult.Failure(TaskError.TaskNotFound(taskId))

        val newTitle = edits.title.resolve(task.title)
        if (newTitle.isNullOrBlank()) return OmniResult.Failure(TaskError.MissingTitle)

        val updated =
            task.copy(
                title = newTitle,
                dueDate = edits.dueDate.resolve(task.dueDate),
                dueTime = edits.dueTime.resolve(task.dueTime),
                priority = edits.priority.resolve(task.priority),
                recurrenceRule = edits.recurrenceRule.resolve(task.recurrenceRule),
                notes = edits.notes.resolve(task.notes),
                envelope = task.envelope.copy(modifiedAt = clock.now()),
            )
        repository.updateTask(updated)
        return OmniResult.Success(updated)
    }
}
