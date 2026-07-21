package com.omnilife.domain.task

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.EntityLifecycleState
import kotlinx.datetime.LocalDate

/**
 * TASK-R-05: filters are a closed set (INV-11), never a free query builder.
 * The Functional Bible also lists a "with/without goal" filter — omitted
 * here because GraphLink/Goal integration (TASK-015) is out of scope this
 * sprint (see the Sprint 1 report's Sprint 2 blockers).
 */
public data class TaskFilter(
    val listId: EntityId? = null,
    val priority: TaskPriority? = null,
    val dueBefore: LocalDate? = null,
    val dueAfter: LocalDate? = null,
    val includeCompleted: Boolean = true,
    val lifecycleState: EntityLifecycleState = EntityLifecycleState.ACTIVE,
)

/** TASK-R-02: default sort is planned time -> priority -> manual order; manual order always wins where set (INV-10). */
public enum class TaskSort {
    DEFAULT,
    DUE_DATE,
    PRIORITY,
    CREATED_AT,
}
