package com.omnilife.feature.task

import com.omnilife.core.common.EntityId
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.usecase.PostponeTarget
import com.omnilife.domain.task.usecase.TaskListMode

/** User intents for the Task list screens (MVI/UDF, TDR-02). */
public sealed interface TaskListIntent {
    public data class ChangeMode(val mode: TaskListMode) : TaskListIntent

    public data object Refresh : TaskListIntent

    public data class Complete(val taskId: EntityId) : TaskListIntent

    /** Resolves a pending [TaskListUiState.pendingSubtaskChoiceForTaskId] (TASK-AC-03). */
    public data class ResolveSubtaskChoice(val taskId: EntityId, val completeOpenSubtasks: Boolean) : TaskListIntent

    public data class Uncomplete(val taskId: EntityId) : TaskListIntent

    public data class Delete(val taskId: EntityId) : TaskListIntent

    /** MFC-R-10/R-11 (Sprint 6): undoes a [Delete] via [TaskListUiState.pendingUndoDelete]. */
    public data class UndoDelete(val taskId: EntityId) : TaskListIntent

    /** The snackbar's own timeout elapsed (or it was superseded) without the user tapping undo. */
    public data object DismissUndoDelete : TaskListIntent

    public data class Postpone(val taskId: EntityId, val target: PostponeTarget) : TaskListIntent

    public data class Reorder(val orderedTaskIds: List<EntityId>) : TaskListIntent

    public data class Search(val query: String) : TaskListIntent

    public data class ChangeFilter(val filter: TaskFilter) : TaskListIntent
}
