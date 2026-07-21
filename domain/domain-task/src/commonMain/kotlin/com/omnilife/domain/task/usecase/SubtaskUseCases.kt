package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.OmniResult
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskRepository

/** TASK-007: internal checklist item, single nesting level only. */
public class AddSubtask(private val repository: TaskRepository, private val newId: () -> EntityId) {
    public suspend operator fun invoke(taskId: EntityId, title: String): OmniResult<Subtask> {
        if (title.isBlank()) return OmniResult.Failure(TaskError.MissingTitle)
        if (repository.findTaskById(taskId) == null) return OmniResult.Failure(TaskError.TaskNotFound(taskId))
        val order = repository.findSubtasks(taskId).size
        val subtask = Subtask(id = newId(), taskId = taskId, title = title, order = order)
        repository.insertSubtask(subtask)
        return OmniResult.Success(subtask)
    }
}

public class ToggleSubtask(private val repository: TaskRepository) {
    public suspend operator fun invoke(subtaskId: EntityId, taskId: EntityId): OmniResult<Subtask> {
        val subtask = repository.findSubtasks(taskId).find { it.id == subtaskId }
            ?: return OmniResult.Failure(TaskError.SubtaskNotFound(subtaskId))
        val updated = subtask.copy(completed = !subtask.completed)
        repository.updateSubtask(updated)
        return OmniResult.Success(updated)
    }
}

public class DeleteSubtask(private val repository: TaskRepository) {
    public suspend operator fun invoke(subtaskId: EntityId): OmniResult<Unit> {
        repository.deleteSubtask(subtaskId)
        return OmniResult.Success(Unit)
    }
}

/** TASK-013 applied to subtasks: manual reorder within the parent task's checklist. */
public class ReorderSubtasks(private val repository: TaskRepository) {
    public suspend operator fun invoke(taskId: EntityId, orderedSubtaskIds: List<EntityId>): OmniResult<Unit> {
        val subtasks = repository.findSubtasks(taskId).associateBy { it.id }
        orderedSubtaskIds.forEachIndexed { index, id ->
            val subtask = subtasks[id] ?: return OmniResult.Failure(TaskError.SubtaskNotFound(id))
            repository.updateSubtask(subtask.copy(order = index))
        }
        return OmniResult.Success(Unit)
    }
}
