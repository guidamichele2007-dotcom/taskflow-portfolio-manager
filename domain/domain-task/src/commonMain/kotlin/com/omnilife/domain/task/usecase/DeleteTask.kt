package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.EventBus
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskRepository
import kotlinx.datetime.Clock

/**
 * MFC-R-09/MFC-R-11: delete moves to trash, 1 gesture + immediate undo — never
 * a confirmation dialog for a single item (that's reserved for bulk actions
 * and permanent deletion, see [PermanentlyDeleteTask]).
 */
public class DeleteTask(
    private val repository: TaskRepository,
    private val eventBus: EventBus,
    private val clock: Clock = Clock.System,
) {
    public suspend operator fun invoke(taskId: EntityId): OmniResult<Task> {
        val task = repository.findTaskById(taskId) ?: return OmniResult.Failure(TaskError.TaskNotFound(taskId))
        val now = clock.now()
        val trashed = task.copy(
            envelope = task.envelope.copy(
                lifecycleState = EntityLifecycleState.TRASHED,
                trashedAt = now,
                modifiedAt = now,
            ),
        )
        repository.updateTask(trashed)
        eventBus.publish(TaskEvent.Deleted(taskId, now))
        return OmniResult.Success(trashed)
    }
}

/** MFC-R-10: restore from trash returns to the original list, identical to before. */
public class RestoreTask(private val repository: TaskRepository, private val clock: Clock = Clock.System) {
    public suspend operator fun invoke(taskId: EntityId): OmniResult<Task> {
        val task = repository.findTaskById(taskId) ?: return OmniResult.Failure(TaskError.TaskNotFound(taskId))
        val now = clock.now()
        val restored = task.copy(
            envelope = task.envelope.copy(
                lifecycleState = EntityLifecycleState.ACTIVE,
                trashedAt = null,
                modifiedAt = now,
            ),
        )
        repository.updateTask(restored)
        return OmniResult.Success(restored)
    }
}

/**
 * Permanent deletion — the one Task operation that needs strong confirmation
 * (CMP-DIALOG), never an undo. The confirmation itself is a UI concern
 * (feature-task); this use case performs the irreversible operation once the
 * UI has already obtained it.
 */
public class PermanentlyDeleteTask(private val repository: TaskRepository) {
    public suspend operator fun invoke(taskId: EntityId): OmniResult<Unit> {
        repository.permanentlyDeleteSubtasksForTask(taskId)
        repository.permanentlyDeleteTask(taskId)
        return OmniResult.Success(Unit)
    }
}
