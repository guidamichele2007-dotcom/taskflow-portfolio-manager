package com.omnilife.domain.task

import com.omnilife.core.common.DomainError

/** Task-module errors (TDR-21) — the vocabulary use cases return in [com.omnilife.core.common.OmniResult.Failure]. */
public sealed class TaskError(override val message: String) : DomainError {
    /** TASK-R-01: title is the only mandatory field. */
    public data object MissingTitle : TaskError("Il titolo è obbligatorio")

    public data class TaskNotFound(val id: String) : TaskError("Nessun task con id $id")

    public data class TaskListNotFound(val id: String) : TaskError("Nessuna lista con id $id")

    public data class SubtaskNotFound(val id: String) : TaskError("Nessun sottotask con id $id")

    /** TASK-AC-03: completing a parent with open subtasks requires an explicit choice. */
    public data object OpenSubtasksRequireChoice :
        TaskError("Il task ha sottotask aperti: scegli se completarli o lasciarli aperti")

    /** MFC-R-11-adjacent: batch operations above the documented threshold need strong confirmation. */
    public data class BulkActionRequiresConfirmation(val itemCount: Int) :
        TaskError("Azione massiva su $itemCount elementi: richiede conferma esplicita")
}
