package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.Envelope
import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.EventBus
import com.omnilife.domain.task.RecurrenceRule
import com.omnilife.domain.task.ReminderConfig
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskPriority
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** TASK-002..006/011: the optional fields of a new task — everything but the mandatory [title]/[listId]. */
public data class NewTaskDetails(
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: TaskPriority = TaskPriority.NONE,
    val recurrenceRule: RecurrenceRule? = null,
    val notes: String? = null,
    val reminderConfig: ReminderConfig? = null,
)

/**
 * TASK-001: creation. [title] is the only mandatory field (TASK-R-01) —
 * everything else defaults per the Functional Bible.
 */
public class CreateTask(
    private val repository: TaskRepository,
    private val eventBus: EventBus,
    private val newId: () -> EntityId,
    private val clock: Clock = Clock.System,
) {
    public suspend operator fun invoke(
        title: String,
        listId: EntityId,
        ownerAccountId: String,
        deviceId: String,
        details: NewTaskDetails = NewTaskDetails(),
    ): OmniResult<Task> {
        if (title.isBlank()) return OmniResult.Failure(TaskError.MissingTitle)

        val now = clock.now()
        val task =
            Task(
                envelope =
                    Envelope(
                        id = newId(),
                        ownerAccountId = ownerAccountId,
                        schemaVersion = 1,
                        createdAt = now,
                        createdByDevice = deviceId,
                        modifiedAt = now,
                        modifiedByDevice = deviceId,
                    ),
                title = title,
                dueDate = details.dueDate,
                dueTime = details.dueTime,
                priority = details.priority,
                recurrenceRule = details.recurrenceRule,
                listId = listId,
                notes = details.notes,
                reminderConfig = details.reminderConfig,
            )
        repository.insertTask(task)
        eventBus.publish(TaskEvent.Created(task.envelope.id, now))
        return OmniResult.Success(task)
    }
}
