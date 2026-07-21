package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.EventBus
import com.omnilife.domain.task.RecurrenceCalculator
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * TASK-008: completion, 1 tap. TASK-AC-03: completing a task with open
 * subtasks requires an explicit choice — never silently modified.
 *
 * TASK-004/INV-15: completing a recurring task lazily generates exactly one
 * next occurrence (a new [Task], never a retroactive edit of past ones,
 * TASK-R-08). This sprint generates it as a fresh entity on this single
 * device; cross-device idempotent generation for the same (rule, period) —
 * TASK-AC-05 — requires the Sync Engine's convergence guarantees and is a
 * Sprint 2 blocker (see the Sprint 1 report).
 */
public class CompleteTask(
    private val repository: TaskRepository,
    private val eventBus: EventBus,
    private val newId: () -> EntityId,
    private val clock: Clock = Clock.System,
) {
    public suspend operator fun invoke(
        taskId: EntityId,
        completeOpenSubtasks: Boolean? = null,
    ): OmniResult<Task> {
        val task = repository.findTaskById(taskId) ?: return OmniResult.Failure(TaskError.TaskNotFound(taskId))
        if (task.completed) return OmniResult.Success(task)

        val openSubtasks = repository.findSubtasks(taskId).filter { !it.completed }
        if (openSubtasks.isNotEmpty() && completeOpenSubtasks == null) {
            return OmniResult.Failure(TaskError.OpenSubtasksRequireChoice)
        }
        if (completeOpenSubtasks == true) {
            openSubtasks.forEach { repository.updateSubtask(it.copy(completed = true)) }
        }

        val now = clock.now()
        val updated =
            task.copy(
                completed = true,
                completedAt = now,
                envelope = task.envelope.copy(modifiedAt = now),
            )
        repository.updateTask(updated)
        eventBus.publish(TaskEvent.Completed(taskId, now))

        task.recurrenceRule?.let { rule ->
            val anchor = task.dueDate ?: clock.todayIn(TimeZone.currentSystemDefault())
            val nextDueDate = RecurrenceCalculator.nextOccurrence(rule, anchor)
            val nextOccurrence =
                task.copy(
                    envelope =
                        task.envelope.copy(
                            id = newId(),
                            createdAt = now,
                            modifiedAt = now,
                        ),
                    dueDate = nextDueDate,
                    completed = false,
                    completedAt = null,
                )
            repository.insertTask(nextOccurrence)
            eventBus.publish(TaskEvent.Created(nextOccurrence.envelope.id, now))
        }

        return OmniResult.Success(updated)
    }
}
