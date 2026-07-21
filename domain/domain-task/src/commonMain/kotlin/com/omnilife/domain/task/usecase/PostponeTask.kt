package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.EventBus
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/** TASK-009: quick reschedule shortcuts (UX Bible FLOW-TASK-01 step 5). */
public sealed interface PostponeTarget {
    public data object Tonight : PostponeTarget
    public data object Tomorrow : PostponeTarget
    public data object Weekend : PostponeTarget
    public data class SpecificDate(val date: LocalDate) : PostponeTarget
}

public class PostponeTask(
    private val repository: TaskRepository,
    private val eventBus: EventBus,
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    public suspend operator fun invoke(taskId: EntityId, target: PostponeTarget): OmniResult<Task> {
        val task = repository.findTaskById(taskId) ?: return OmniResult.Failure(TaskError.TaskNotFound(taskId))
        val today = clock.todayIn(timeZone)
        val newDueDate = when (target) {
            is PostponeTarget.Tonight -> today
            is PostponeTarget.Tomorrow -> today.plus(1, DateTimeUnit.DAY)
            is PostponeTarget.Weekend -> nextSaturday(today)
            is PostponeTarget.SpecificDate -> target.date
        }
        val now = clock.now()
        val updated = task.copy(dueDate = newDueDate, envelope = task.envelope.copy(modifiedAt = now))
        repository.updateTask(updated)
        eventBus.publish(TaskEvent.Rescheduled(taskId, now, newDueDate))
        return OmniResult.Success(updated)
    }

    private fun nextSaturday(from: LocalDate): LocalDate {
        var candidate = from
        while (candidate.dayOfWeek != DayOfWeek.SATURDAY) {
            candidate = candidate.plus(1, DateTimeUnit.DAY)
        }
        return candidate
    }
}
