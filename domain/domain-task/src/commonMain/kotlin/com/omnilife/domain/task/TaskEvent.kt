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

    /** `task.overdue.count.changed` — a list-level count, not tied to one task id. */
    public data class OverdueCountChanged(val newOverdueCount: Int, val at: Instant) : TaskEvent
}
