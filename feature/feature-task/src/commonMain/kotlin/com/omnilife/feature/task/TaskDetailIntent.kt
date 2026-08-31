package com.omnilife.feature.task

import com.omnilife.core.common.EntityId
import com.omnilife.domain.task.RecurrenceRule
import com.omnilife.domain.task.TaskPriority
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** User intents for IA-035 (Dettaglio task). Every field edit autosaves (MFC-R-06) — no explicit "save" intent. */
public sealed interface TaskDetailIntent {
    public data object Load : TaskDetailIntent

    public data class ChangeTitle(val title: String) : TaskDetailIntent

    public data class ChangeDueDate(val date: LocalDate?, val time: LocalTime?) : TaskDetailIntent

    public data class ChangePriority(val priority: TaskPriority) : TaskDetailIntent

    public data class ChangeRecurrence(val rule: RecurrenceRule?) : TaskDetailIntent

    public data class ChangeNotes(val notes: String?) : TaskDetailIntent

    public data class ChangeNewSubtaskTitle(val title: String) : TaskDetailIntent

    public data object AddSubtask : TaskDetailIntent

    public data class ToggleSubtask(val subtaskId: EntityId) : TaskDetailIntent

    public data class DeleteSubtask(val subtaskId: EntityId) : TaskDetailIntent

    public data class ReorderSubtasks(val orderedSubtaskIds: List<EntityId>) : TaskDetailIntent

    public data object Delete : TaskDetailIntent
}
