package com.omnilife.feature.task.bridge

import com.omnilife.domain.task.Task
import kotlinx.serialization.Serializable

/**
 * The outbox wire shape for a [Task] snapshot — every field flattened to a primitive/string so it
 * serializes without wiring `kotlinx-datetime`'s contextual serializers into this module. A full
 * field-level CRDT delta (per [com.omnilife.core.sync.DeltaGenerator]) is out of this sprint's
 * scope — see `TaskSyncOutboxBridge`'s doc.
 */
@Serializable
internal data class TaskSyncPayload(
    val id: String,
    val ownerAccountId: String,
    val modifiedAt: String,
    val lifecycleState: String,
    val title: String,
    val dueDate: String?,
    val dueTime: String?,
    val priority: String,
    val listId: String,
    val notes: String?,
    val completed: Boolean,
)

internal fun Task.toSyncPayload(): TaskSyncPayload =
    TaskSyncPayload(
        id = envelope.id,
        ownerAccountId = envelope.ownerAccountId,
        modifiedAt = envelope.modifiedAt.toString(),
        lifecycleState = envelope.lifecycleState.name,
        title = title,
        dueDate = dueDate?.toString(),
        dueTime = dueTime?.toString(),
        priority = priority.name,
        listId = listId,
        notes = notes,
        completed = completed,
    )
