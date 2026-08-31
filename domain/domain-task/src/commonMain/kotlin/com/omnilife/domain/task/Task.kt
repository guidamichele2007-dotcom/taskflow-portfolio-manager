package com.omnilife.domain.task

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.Envelope
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * DM-TASK-01: a Task is a volitional commitment (a thing to do) — the
 * entity with the smallest mandatory field set in the whole product
 * (TASK-R-01: [title] is the only required field).
 */
public data class Task(
    val envelope: Envelope,
    val title: String,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: TaskPriority = TaskPriority.NONE,
    val recurrenceRule: RecurrenceRule? = null,
    val listId: EntityId,
    val notes: String? = null,
    val completed: Boolean = false,
    val completedAt: Instant? = null,
    /** Always wins over the default sort when present (INV-10, TASK-013). */
    val manualOrder: Int? = null,
    val reminderConfig: ReminderConfig? = null,
) {
    init {
        require(title.isNotBlank()) { "Task title must not be blank (TASK-R-01)" }
    }

    /** TASK-R-03: a task with no due date never expires and never nags. */
    public fun isOverdue(today: LocalDate): Boolean = dueDate != null && dueDate < today && !completed
}
