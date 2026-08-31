package com.omnilife.feature.task

import com.omnilife.core.common.EntityId
import com.omnilife.domain.task.TaskPriority
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * MVI state for Quick Capture / create-task (Macro Sprint 5's "Home → Quick Capture/Create Task"
 * flow). Deliberately a small, scoped-down slice of `feature-capture`'s eventual full multi-entity
 * capture system — see sprint5_report.md for why a separate, task-specific ViewModel was chosen
 * for this sprint instead of building out CAPT.
 */
public data class TaskCreateUiState(
    val title: String = "",
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: TaskPriority = TaskPriority.NONE,
    val notes: String? = null,
    /** Only meaningful once [dueDate] and [dueTime] are both set (ReminderConfig's own constraint). */
    val reminderEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    /** Non-null once save succeeds — the UI navigates to this task's detail sheet and clears the form. */
    val createdTaskId: EntityId? = null,
) {
    public val canSave: Boolean get() = title.isNotBlank() && !isSaving
}
