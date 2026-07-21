package com.omnilife.domain.task

import com.omnilife.core.common.Envelope

/**
 * DM-TASK-02: the 2-level grouping container (Area -> Lista, TASK-005,
 * Product Bible P32). Only whole lists archive (TASK-018) — individual
 * tasks never archive on their own (they complete or trash).
 *
 * `area` is a stable text label, not an independent entity with its own
 * lifecycle (MDEC-04, Data Model Bible §03) — deliberately not modeled as a
 * separate type.
 */
public data class TaskList(
    val envelope: Envelope,
    val name: String,
    val area: String? = null,
    /** The initial "Attività" list: not deletable, only renamable. */
    val isDefault: Boolean = false,
    val manualOrder: Int? = null,
) {
    init {
        require(name.isNotBlank()) { "TaskList name must not be blank" }
    }
}
