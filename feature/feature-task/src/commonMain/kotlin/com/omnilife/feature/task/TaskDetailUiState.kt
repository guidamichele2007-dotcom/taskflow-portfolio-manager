package com.omnilife.feature.task

import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task

/** MVI state for IA-035 (Dettaglio task — CMP-SHEET). */
public data class TaskDetailUiState(
    val isLoading: Boolean = true,
    val task: Task? = null,
    val subtasks: List<Subtask> = emptyList(),
    val newSubtaskTitle: String = "",
    val errorMessage: String? = null,
    /** MUC §5: the entity was deleted elsewhere while this sheet was open -> gentle auto-close. */
    val noLongerAvailable: Boolean = false,
)
