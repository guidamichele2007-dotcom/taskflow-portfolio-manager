package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.EventBus
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock

/** Undo of [CompleteTask] (GEF undo pattern, 7s snackbar — the snackbar itself is a UI concern, feature-task). */
public class UncompleteTask(
    private val repository: TaskRepository,
    private val eventBus: EventBus,
    private val clock: Clock = Clock.System,
) {
    public suspend operator fun invoke(taskId: EntityId): OmniResult<Task> {
        val task = repository.findTaskById(taskId) ?: return OmniResult.Failure(TaskError.TaskNotFound(taskId))
        val now = clock.now()
        val updated = task.copy(completed = false, completedAt = null, envelope = task.envelope.copy(modifiedAt = now))
        repository.updateTask(updated)
        eventBus.publish(TaskEvent.Uncompleted(taskId, now))
        return OmniResult.Success(updated)
    }
}
