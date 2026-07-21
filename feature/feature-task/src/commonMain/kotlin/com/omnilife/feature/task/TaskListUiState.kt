package com.omnilife.feature.task

import com.omnilife.core.common.EntityId
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.usecase.TaskListMode

/**
 * MVI state for IA-030/031/032/034 (Oggi/Prossimi/Liste/In sospeso — TDR-02).
 * Immutable by construction; every field a screen needs is here, nothing
 * computed by the UI layer itself (Technical Architecture Bible §01 §4: L1
 * never contains business logic).
 */
public data class TaskListUiState(
    val mode: TaskListMode = TaskListMode.TODAY,
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val filter: TaskFilter = TaskFilter(),
    val errorMessage: String? = null,
    /** Non-null while the UI must ask "complete all subtasks / keep open" (TASK-AC-03). */
    val pendingSubtaskChoiceForTaskId: EntityId? = null,
) {
    /** MUC §6: empty-never-used vs empty-filtered are visually distinct states. */
    public val isEmpty: Boolean get() = !isLoading && tasks.isEmpty()
    public val isFiltered: Boolean get() = searchQuery.isNotBlank() || filter != TaskFilter()
}
