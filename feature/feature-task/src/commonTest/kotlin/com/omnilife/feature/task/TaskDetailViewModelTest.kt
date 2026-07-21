package com.omnilife.feature.task

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskPriority
import com.omnilife.domain.task.usecase.AddSubtask
import com.omnilife.domain.task.usecase.DeleteSubtask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.ReorderSubtasks
import com.omnilife.domain.task.usecase.ToggleSubtask
import com.omnilife.domain.task.usecase.UpdateTaskFields
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskDetailViewModelTest {
    private fun viewModel(
        repository: FakeTaskRepository,
        taskId: String,
        scope: CoroutineScope,
    ) = TaskDetailViewModel(
        taskId = taskId,
        repository = repository,
        updateTaskFields = UpdateTaskFields(repository),
        deleteTask = DeleteTask(repository, InMemoryEventBus()),
        addSubtask = AddSubtask(repository, newId = { "sub-generated" }),
        toggleSubtask = ToggleSubtask(repository),
        deleteSubtask = DeleteSubtask(repository),
        reorderSubtasks = ReorderSubtasks(repository),
        scope = scope,
    )

    private fun repositoryWithParentTask(): FakeTaskRepository = FakeTaskRepository().apply {
        tasks["task-1"] = Task(envelope = testEnvelopeFixture("task-1"), title = "Parent", listId = "list-1")
    }

    @Test
    fun `loads the task and its subtasks on creation`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val viewModel = viewModel(repositoryWithParentTask(), "task-1", scope)

        assertEquals("Parent", viewModel.state.value.task?.title)
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `a missing task marks the state as no longer available`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val viewModel = viewModel(FakeTaskRepository(), "missing", scope)

        assertTrue(viewModel.state.value.noLongerAvailable)
    }

    @Test
    fun `changing the priority autosaves without an explicit save intent`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = repositoryWithParentTask()
        val viewModel = viewModel(repository, "task-1", scope)

        viewModel.dispatch(TaskDetailIntent.ChangePriority(TaskPriority.HIGH))

        assertEquals(TaskPriority.HIGH, repository.tasks.getValue("task-1").priority)
        assertEquals(TaskPriority.HIGH, viewModel.state.value.task?.priority)
    }

    @Test
    fun `adding a subtask clears the draft title and appends it to the list`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val viewModel = viewModel(repositoryWithParentTask(), "task-1", scope)

        viewModel.dispatch(TaskDetailIntent.ChangeNewSubtaskTitle("Buy milk"))
        viewModel.dispatch(TaskDetailIntent.AddSubtask)

        assertEquals("", viewModel.state.value.newSubtaskTitle)
        assertEquals(listOf("Buy milk"), viewModel.state.value.subtasks.map { it.title })
    }

    @Test
    fun `deleting the task marks the state as no longer available`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val viewModel = viewModel(repositoryWithParentTask(), "task-1", scope)

        viewModel.dispatch(TaskDetailIntent.Delete)

        assertTrue(viewModel.state.value.noLongerAvailable)
    }

    @Test
    fun `toggling a subtask updates its completed flag in the state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val viewModel = viewModel(repositoryWithParentTask(), "task-1", scope)
        viewModel.dispatch(TaskDetailIntent.ChangeNewSubtaskTitle("Step"))
        viewModel.dispatch(TaskDetailIntent.AddSubtask)
        val subtaskId = viewModel.state.value.subtasks.single().id

        viewModel.dispatch(TaskDetailIntent.ToggleSubtask(subtaskId))

        assertTrue(viewModel.state.value.subtasks.single().completed)
    }
}
