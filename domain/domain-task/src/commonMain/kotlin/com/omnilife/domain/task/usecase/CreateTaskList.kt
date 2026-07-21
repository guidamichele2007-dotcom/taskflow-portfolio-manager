package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.Envelope
import com.omnilife.core.common.OmniResult
import com.omnilife.domain.task.TaskList
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock

/** TASK-005: Lista (2-level grouping, Area -> Lista). */
public class CreateTaskList(
    private val repository: TaskRepository,
    private val newId: () -> EntityId,
    private val clock: Clock = Clock.System,
) {
    public suspend operator fun invoke(
        name: String,
        ownerAccountId: String,
        deviceId: String,
        area: String? = null,
    ): OmniResult<TaskList> {
        val now = clock.now()
        val list = TaskList(
            envelope = Envelope(
                id = newId(),
                ownerAccountId = ownerAccountId,
                schemaVersion = 1,
                createdAt = now,
                createdByDevice = deviceId,
                modifiedAt = now,
                modifiedByDevice = deviceId,
            ),
            name = name,
            area = area,
        )
        repository.insertList(list)
        return OmniResult.Success(list)
    }
}
