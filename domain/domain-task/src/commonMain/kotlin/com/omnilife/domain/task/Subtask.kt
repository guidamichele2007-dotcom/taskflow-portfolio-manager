package com.omnilife.domain.task

import com.omnilife.core.common.EntityId

/**
 * DM-TASK-03: a single-level checklist item internal to a [Task] (TASK-007)
 * — not a recursive Task: no own date, priority, or reminder, and no
 * independent lifecycle (it follows its parent Task through every state
 * transition, including trash/restore).
 */
public data class Subtask(
    val id: EntityId,
    val taskId: EntityId,
    val title: String,
    val completed: Boolean = false,
    val order: Int = 0,
) {
    init {
        require(title.isNotBlank()) { "Subtask title must not be blank" }
    }
}
