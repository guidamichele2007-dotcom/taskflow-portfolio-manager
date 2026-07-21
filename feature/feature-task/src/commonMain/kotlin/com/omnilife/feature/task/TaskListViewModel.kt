package com.omnilife.feature.task

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.onFailure
import com.omnilife.core.common.onSuccess
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.usecase.CompleteTask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.GetTasksForView
import com.omnilife.domain.task.usecase.PostponeTask
import com.omnilife.domain.task.usecase.ReorderTasks
import com.omnilife.domain.task.usecase.SearchTasks
import com.omnilife.domain.task.usecase.UncompleteTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI store for IA-030/031/032/034 (TDR-02). Pure Kotlin — no Compose/SwiftUI
 * dependency, so it is fully unit-testable on the JVM target even in this
 * sandbox (see README-BUILD.md §11).
 */
public class TaskListViewModel(
    private val getTasksForView: GetTasksForView,
    private val completeTask: CompleteTask,
    private val uncompleteTask: UncompleteTask,
    private val deleteTask: DeleteTask,
    private val postponeTask: PostponeTask,
    private val reorderTasks: ReorderTasks,
    private val searchTasks: SearchTasks,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(TaskListUiState(isLoading = true))
    public val state: StateFlow<TaskListUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    public fun dispatch(intent: TaskListIntent) {
        when (intent) {
            is TaskListIntent.ChangeMode -> {
                _state.update { it.copy(mode = intent.mode) }
                refresh()
            }

            is TaskListIntent.Refresh -> refresh()

            is TaskListIntent.Complete -> scope.launch { handleComplete(intent.taskId, completeOpenSubtasks = null) }

            is TaskListIntent.ResolveSubtaskChoice ->
                scope.launch {
                    handleComplete(intent.taskId, intent.completeOpenSubtasks)
                }

            is TaskListIntent.Uncomplete ->
                scope.launch {
                    uncompleteTask(intent.taskId)
                    refresh()
                }

            is TaskListIntent.Delete ->
                scope.launch {
                    deleteTask(intent.taskId)
                    refresh()
                }

            is TaskListIntent.Postpone ->
                scope.launch {
                    postponeTask(intent.taskId, intent.target)
                    refresh()
                }

            is TaskListIntent.Reorder ->
                scope.launch {
                    reorderTasks(intent.orderedTaskIds)
                    refresh()
                }

            is TaskListIntent.Search -> {
                _state.update { it.copy(searchQuery = intent.query) }
                scope.launch { runSearch(intent.query) }
            }

            is TaskListIntent.ChangeFilter -> {
                _state.update { it.copy(filter = intent.filter) }
                refresh()
            }
        }
    }

    private suspend fun handleComplete(
        taskId: EntityId,
        completeOpenSubtasks: Boolean?,
    ) {
        val result = completeTask(taskId, completeOpenSubtasks)
        result.onSuccess {
            _state.update { it.copy(pendingSubtaskChoiceForTaskId = null) }
            refresh()
        }.onFailure { error ->
            if (error is TaskError.OpenSubtasksRequireChoice) {
                _state.update { it.copy(pendingSubtaskChoiceForTaskId = taskId) }
            } else {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun refresh() {
        val currentState = _state.value
        _state.update { it.copy(isLoading = true) }
        scope.launch {
            val tasks =
                getTasksForView(
                    mode = currentState.mode,
                    listId = currentState.filter.listId,
                    priority = currentState.filter.priority,
                )
            _state.update { it.copy(isLoading = false, tasks = tasks, errorMessage = null) }
        }
    }

    private suspend fun runSearch(query: String) {
        if (query.isBlank()) {
            refresh()
            return
        }
        val results = searchTasks(query)
        _state.update { it.copy(isLoading = false, tasks = results) }
    }

    /** Cancels all in-flight work; call when the screen owning this store is disposed. */
    public fun clear() {
        scope.coroutineContext[Job]?.cancel()
    }
}
