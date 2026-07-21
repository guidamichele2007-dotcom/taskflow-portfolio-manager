package com.omnilife.domain.task.persistence

import com.omnilife.core.common.Envelope
import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.domain.task.RecurrenceRule
import com.omnilife.domain.task.ReminderConfig
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskList
import com.omnilife.domain.task.TaskPriority
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal val taskJson = Json { ignoreUnknownKeys = true }

internal fun TaskEntity.toDomain(): Task = Task(
    envelope = Envelope(
        id = id,
        ownerAccountId = ownerAccountId,
        schemaVersion = schemaVersion.toInt(),
        createdAt = Instant.parse(createdAt),
        createdByDevice = createdByDevice,
        modifiedAt = Instant.parse(modifiedAt),
        modifiedByDevice = modifiedByDevice,
        lifecycleState = EntityLifecycleState.valueOf(lifecycleState),
        trashedAt = trashedAt?.let(Instant::parse),
    ),
    title = title,
    dueDate = dueDate?.let(LocalDate::parse),
    dueTime = dueTime?.let(LocalTime::parse),
    priority = TaskPriority.valueOf(priority),
    recurrenceRule = recurrenceRuleJson?.let { taskJson.decodeFromString(RecurrenceRule.serializer(), it) },
    listId = listId,
    notes = notes,
    completed = completed == 1L,
    completedAt = completedAt?.let(Instant::parse),
    manualOrder = manualOrder?.toInt(),
    reminderConfig = reminderConfigJson?.let { taskJson.decodeFromString(ReminderConfig.serializer(), it) },
)

internal fun Task.toRow(): TaskEntity = TaskEntity(
    id = envelope.id,
    ownerAccountId = envelope.ownerAccountId,
    schemaVersion = envelope.schemaVersion.toLong(),
    createdAt = envelope.createdAt.toString(),
    createdByDevice = envelope.createdByDevice,
    modifiedAt = envelope.modifiedAt.toString(),
    modifiedByDevice = envelope.modifiedByDevice,
    lifecycleState = envelope.lifecycleState.name,
    trashedAt = envelope.trashedAt?.toString(),
    title = title,
    dueDate = dueDate?.toString(),
    dueTime = dueTime?.toString(),
    priority = priority.name,
    recurrenceRuleJson = recurrenceRule?.let { taskJson.encodeToString(RecurrenceRule.serializer(), it) },
    listId = listId,
    notes = notes,
    completed = if (completed) 1L else 0L,
    completedAt = completedAt?.toString(),
    manualOrder = manualOrder?.toLong(),
    reminderConfigJson = reminderConfig?.let { taskJson.encodeToString(ReminderConfig.serializer(), it) },
)

internal fun TaskListEntity.toDomain(): TaskList = TaskList(
    envelope = Envelope(
        id = id,
        ownerAccountId = ownerAccountId,
        schemaVersion = schemaVersion.toInt(),
        createdAt = Instant.parse(createdAt),
        createdByDevice = createdByDevice,
        modifiedAt = Instant.parse(modifiedAt),
        modifiedByDevice = modifiedByDevice,
        lifecycleState = EntityLifecycleState.valueOf(lifecycleState),
        trashedAt = trashedAt?.let(Instant::parse),
    ),
    name = name,
    area = area,
    isDefault = isDefault == 1L,
    manualOrder = manualOrder?.toInt(),
)

internal fun TaskList.toRow(): TaskListEntity = TaskListEntity(
    id = envelope.id,
    ownerAccountId = envelope.ownerAccountId,
    schemaVersion = envelope.schemaVersion.toLong(),
    createdAt = envelope.createdAt.toString(),
    createdByDevice = envelope.createdByDevice,
    modifiedAt = envelope.modifiedAt.toString(),
    modifiedByDevice = envelope.modifiedByDevice,
    lifecycleState = envelope.lifecycleState.name,
    trashedAt = envelope.trashedAt?.toString(),
    name = name,
    area = area,
    isDefault = if (isDefault) 1L else 0L,
    manualOrder = manualOrder?.toLong(),
)

internal fun SubtaskEntity.toDomain(): Subtask = Subtask(
    id = id,
    taskId = taskId,
    title = title,
    completed = completed == 1L,
    order = orderIndex.toInt(),
)

internal fun Subtask.toRow(): SubtaskEntity = SubtaskEntity(
    id = id,
    taskId = taskId,
    title = title,
    completed = if (completed) 1L else 0L,
    orderIndex = order.toLong(),
)
