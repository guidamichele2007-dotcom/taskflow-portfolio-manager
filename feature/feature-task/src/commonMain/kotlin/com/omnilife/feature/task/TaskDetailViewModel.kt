package com.omnilife.feature.task

import com.omnilife.core.common.DomainError
import com.omnilife.core.common.EntityId
import com.omnilife.core.common.onFailure
import com.omnilife.domain.task.TaskRepository
import com.omnilife.domain.task.usecase.AddSubtask
import com.omnilife.domain.task.usecase.DeleteSubtask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.Edit
import com.omnilife.domain.task.usecase.ReorderSubtasks
import com.omnilife.domain.task.usecase.TaskFieldEdits
import com.omnilife.domain.task.usecase.ToggleSubtask
import com.omnilife.domain.task.usecase.UpdateTaskFields
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** MVI store for IA-035 (Dettaglio task, expandable bottom sheet — CMP-SHEET). */
public class TaskDetailViewModel(
    private val taskId: EntityId,
    private val repository: TaskRepository,
    private val updateTaskFields: UpdateTaskFields,
    private val deleteTask: DeleteTask,
    private val addSubtask: AddSubtask,
    private val toggleSubtask: ToggleSubtask,
    private val deleteSubtask: DeleteSubtask,
    private val reorderSubtasks: ReorderSubtasks,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(TaskDetailUiState())
    public val state: StateFlow<TaskDetailUiState> = _state.asStateFlow()

    init {
        dispatch(TaskDetailIntent.Load)
    }

    public fun dispatch(intent: TaskDetailIntent) {
        when (intent) {
            TaskDetailIntent.Load -> scope.launch { load() }
            is TaskDetailIntent.ChangeTitle ->
                scope.launch {
                    updateTaskFields(taskId, TaskFieldEdits(title = Edit.Set(intent.title))).onFailure(::reportError)
                    load()
                }

            is TaskDetailIntent.ChangeDueDate ->
                scope.launch {
                    updateTaskFields(
                        taskId,
                        TaskFieldEdits(dueDate = Edit.Set(intent.date), dueTime = Edit.Set(intent.time)),
                    ).onFailure(::reportError)
                    load()
                }

            is TaskDetailIntent.ChangePriority ->
                scope.launch {
                    updateTaskFields(taskId, TaskFieldEdits(priority = Edit.Set(intent.priority)))
                        .onFailure(::reportError)
                    load()
                }

            is TaskDetailIntent.ChangeRecurrence ->
                scope.launch {
                    updateTaskFields(taskId, TaskFieldEdits(recurrenceRule = Edit.Set(intent.rule)))
                        .onFailure(::reportError)
                    load()
                }

            is TaskDetailIntent.ChangeNotes ->
                scope.launch {
                    updateTaskFields(taskId, TaskFieldEdits(notes = Edit.Set(intent.notes))).onFailure(::reportError)
                    load()
                }

            is TaskDetailIntent.ChangeNewSubtaskTitle -> _state.update { it.copy(newSubtaskTitle = intent.title) }

            TaskDetailIntent.AddSubtask ->
                scope.launch {
                    val title = _state.value.newSubtaskTitle
                    if (title.isNotBlank()) {
                        addSubtask(taskId, title).onFailure(::reportError)
                        _state.update { it.copy(newSubtaskTitle = "") }
                        load()
                    }
                }

            is TaskDetailIntent.ToggleSubtask ->
                scope.launch {
                    toggleSubtask(intent.subtaskId, taskId).onFailure(::reportError)
                    load()
                }

            is TaskDetailIntent.DeleteSubtask ->
                scope.launch {
                    deleteSubtask(intent.subtaskId).onFailure(::reportError)
                    load()
                }

            is TaskDetailIntent.ReorderSubtasks ->
                scope.launch {
                    reorderSubtasks(taskId, intent.orderedSubtaskIds).onFailure(::reportError)
                    load()
                }

            TaskDetailIntent.Delete ->
                scope.launch {
                    deleteTask(taskId).onFailure(::reportError)
                    _state.update { it.copy(noLongerAvailable = true) }
                }
        }
    }

    private fun reportError(error: DomainError) {
        _state.update { it.copy(errorMessage = error.message) }
    }

    private suspend fun load() {
        val task = repository.findTaskById(taskId)
        if (task == null) {
            _state.update { it.copy(isLoading = false, noLongerAvailable = true) }
            return
        }
        val subtasks = repository.findSubtasks(taskId)
        _state.update { it.copy(isLoading = false, task = task, subtasks = subtasks, errorMessage = null) }
    }

    public fun clear() {
        scope.coroutineContext[Job]?.cancel()
    }
}
