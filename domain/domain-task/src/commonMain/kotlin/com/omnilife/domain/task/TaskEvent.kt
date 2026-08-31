package com.omnilife.domain.task

import com.omnilife.core.common.EntityId
import com.omnilife.core.eventbus.DomainEvent
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Events the Task module publishes on the Bus Eventi (Functional Bible §6,
 * `modulo.entita.azione` naming). Payload is minimal by construction
 * (Technical Architecture Bible §03 §3) — id, type-relevant fields, and a
 * timestamp, never the full entity.
 *
 * Subscriptions the Bible also lists for this module (`capt.item.captured`,
 * `cal.day.load.changed`, `core.day.changed`) are not wired in this sprint:
 * their producer modules (Capture, Calendar, a Core "day changed" source)
 * are out of scope — see the Sprint 1 report's Sprint 2 blockers.
 */
public sealed interface TaskEvent : DomainEvent {
    public data class Created(val taskId: EntityId, val at: Instant) : TaskEvent

    public data class Completed(val taskId: EntityId, val at: Instant) : TaskEvent

    public data class Uncompleted(val taskId: EntityId, val at: Instant) : TaskEvent

    /**
     * `task.item.updated` (TDR-35): a field edit via [com.omnilife.domain.task.usecase.UpdateTaskFields].
     * Added this sprint — the use case previously published nothing, which left consumers that
     * must stay consistent with edits (the search index bridge, in particular) with no signal.
     * Deliberately as minimal as every other event here: just enough for a subscriber to know it
     * must re-read the task, never the edited fields themselves.
     */
    public data class Updated(val taskId: EntityId, val at: Instant) : TaskEvent

    public data class Rescheduled(val taskId: EntityId, val at: Instant, val newDueDate: LocalDate?) : TaskEvent

    public data class Deleted(val taskId: EntityId, val at: Instant) : TaskEvent

    /**
     * `task.item.restored` (Sprint 6): trash recovery via [com.omnilife.domain.task.usecase.RestoreTask].
     * Added after finding `RestoreTask` published nothing — the search index kept the task marked
     * TRASHED, its reminder was never rescheduled, and the outbox never queued the restore for
     * sync, all silently, until the next unrelated edit. Same minimal shape as every other event.
     */
    public data class Restored(val taskId: EntityId, val at: Instant) : TaskEvent

    /** `task.overdue.count.changed` — a list-level count, not tied to one task id. */
    public data class OverdueCountChanged(val newOverdueCount: Int, val at: Instant) : TaskEvent

    /**
     * `task.item.permanently-deleted` (Sprint 6): published by
     * [com.omnilife.domain.task.usecase.PermanentlyDeleteTask], whose row is gone from the
     * repository by the time this fires — unlike every other event here, a subscriber must NOT
     * try to re-read the task; the id is all there is. Was previously unpublished (a documented
     * Sprint 5 residual risk): the search index kept an orphaned row for a permanently-deleted
     * task forever, a ghost result if `includeArchivedOrTrashed` is ever used.
     */
    public data class PermanentlyDeleted(val taskId: EntityId, val at: Instant) : TaskEvent
}
