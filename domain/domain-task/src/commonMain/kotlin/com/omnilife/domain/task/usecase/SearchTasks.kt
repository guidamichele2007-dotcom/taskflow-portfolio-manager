package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskRepository

/**
 * TASK-R-05 local text search (within this module — not the cross-module
 * SRCH-* feature, see README-BUILD.md and the Sprint 1 report).
 */
public class SearchTasks(private val repository: TaskRepository) {
    public suspend operator fun invoke(
        query: String,
        includeArchived: Boolean = false,
    ): List<Task> {
        if (query.isBlank()) return emptyList()
        val active = repository.searchTasks(query, EntityLifecycleState.ACTIVE)
        if (!includeArchived) return active
        val archived = repository.searchTasks(query, EntityLifecycleState.ARCHIVED)
        return active + archived
    }
}
