package com.omnilife.feature.task

import com.omnilife.domain.task.TaskPriority
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** User intents for Quick Capture / create-task. */
public sealed interface TaskCreateIntent {
    public data class ChangeTitle(val title: String) : TaskCreateIntent

    public data class ChangeDueDate(val date: LocalDate?, val time: LocalTime?) : TaskCreateIntent

    public data class ChangePriority(val priority: TaskPriority) : TaskCreateIntent

    public data class ChangeNotes(val notes: String?) : TaskCreateIntent

    public data class ToggleReminder(val enabled: Boolean) : TaskCreateIntent

    public data object Save : TaskCreateIntent

    /** Resets the form after a successful save's [TaskCreateUiState.createdTaskId] has been consumed by navigation. */
    public data object ConsumeCreated : TaskCreateIntent
}
