package com.omnilife.feature.task

import com.omnilife.core.common.DomainError
import com.omnilife.core.common.EntityId
import com.omnilife.core.common.onFailure
import com.omnilife.core.common.onSuccess
import com.omnilife.domain.task.ReminderConfig
import com.omnilife.domain.task.usecase.CreateTask
import com.omnilife.domain.task.usecase.NewTaskDetails
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
 * MVI store for Quick Capture / create-task (TDR-02). [listId]/[ownerAccountId]/[deviceId] are
 * resolved once by the composition root (the default "Attività" list, current account/device) —
 * this ViewModel itself makes no account/device policy decisions, same separation as every other
 * ViewModel in this module.
 */
public class TaskCreateViewModel(
    private val createTask: CreateTask,
    private val listId: EntityId,
    private val ownerAccountId: String,
    private val deviceId: String,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(TaskCreateUiState())
    public val state: StateFlow<TaskCreateUiState> = _state.asStateFlow()

    public fun dispatch(intent: TaskCreateIntent) {
        when (intent) {
            is TaskCreateIntent.ChangeTitle -> _state.update { it.copy(title = intent.title, errorMessage = null) }

            is TaskCreateIntent.ChangeDueDate ->
                _state.update { it.copy(dueDate = intent.date, dueTime = intent.time) }

            is TaskCreateIntent.ChangePriority -> _state.update { it.copy(priority = intent.priority) }

            is TaskCreateIntent.ChangeNotes -> _state.update { it.copy(notes = intent.notes) }

            is TaskCreateIntent.ToggleReminder -> _state.update { it.copy(reminderEnabled = intent.enabled) }

            TaskCreateIntent.Save -> save()

            TaskCreateIntent.ConsumeCreated -> _state.value = TaskCreateUiState()
        }
    }

    private fun save() {
        val current = _state.value
        if (!current.canSave) return
        _state.update { it.copy(isSaving = true) }
        scope.launch {
            val reminderConfig =
                if (current.reminderEnabled && current.dueDate != null && current.dueTime != null) {
                    ReminderConfig()
                } else {
                    null
                }
            val result =
                createTask(
                    title = current.title,
                    listId = listId,
                    ownerAccountId = ownerAccountId,
                    deviceId = deviceId,
                    details =
                        NewTaskDetails(
                            dueDate = current.dueDate,
                            dueTime = current.dueTime,
                            priority = current.priority,
                            notes = current.notes,
                            reminderConfig = reminderConfig,
                        ),
                )
            result
                .onSuccess { task -> _state.update { it.copy(isSaving = false, createdTaskId = task.envelope.id) } }
                .onFailure(::reportError)
        }
    }

    private fun reportError(error: DomainError) {
        _state.update { it.copy(isSaving = false, errorMessage = error.message) }
    }

    /** Cancels all in-flight work; call when the screen owning this store is disposed. */
    public fun clear() {
        scope.coroutineContext[Job]?.cancel()
    }
}
