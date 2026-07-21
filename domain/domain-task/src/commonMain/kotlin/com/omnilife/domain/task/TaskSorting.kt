package com.omnilife.domain.task

/**
 * Applies [TaskSort] to an already-filtered list. Extracted as a single
 * shared function (rather than duplicated per [TaskRepository]
 * implementation) so a real backend and any test double sort identically —
 * see README-BUILD.md §11 on why fakes live per-module rather than in a
 * shared test-fixtures module for now; this at least keeps the one piece of
 * actual logic they'd otherwise duplicate in one place.
 */
public fun List<Task>.sortedByTaskSort(sort: TaskSort): List<Task> =
    when (sort) {
        TaskSort.DEFAULT ->
            sortedWith(
                compareBy<Task> { it.manualOrder == null }
                    .thenBy { it.manualOrder }
                    .thenBy { it.dueDate == null }
                    .thenBy { it.dueDate }
                    .thenByDescending { it.priority.ordinal },
            )

        TaskSort.DUE_DATE -> sortedWith(compareBy({ it.dueDate == null }, { it.dueDate }))
        TaskSort.PRIORITY -> sortedByDescending { it.priority.ordinal }
        TaskSort.CREATED_AT -> sortedBy { it.envelope.createdAt }
    }
