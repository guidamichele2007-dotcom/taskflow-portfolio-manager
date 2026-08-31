package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.OmniResult
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock

/** TASK-013: manual drag reorder within a list or within the Today view. Always wins over the default sort (INV-10). */
public class ReorderTasks(private val repository: TaskRepository, private val clock: Clock = Clock.System) {
    public suspend operator fun invoke(orderedTaskIds: List<EntityId>): OmniResult<Unit> {
        val now = clock.now()
        orderedTaskIds.forEachIndexed { index, id ->
            val task = repository.findTaskById(id) ?: return OmniResult.Failure(TaskError.TaskNotFound(id))
            repository.updateTask(task.copy(manualOrder = index, envelope = task.envelope.copy(modifiedAt = now)))
        }
        return OmniResult.Success(Unit)
    }
}
