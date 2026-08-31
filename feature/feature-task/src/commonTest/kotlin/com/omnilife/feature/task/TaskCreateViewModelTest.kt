package com.omnilife.feature.task

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.TaskPriority
import com.omnilife.domain.task.usecase.CreateTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TaskCreateViewModelTest {
    private fun viewModel(repository: FakeTaskRepository = FakeTaskRepository()): TaskCreateViewModel {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        return TaskCreateViewModel(
            createTask = CreateTask(repository, InMemoryEventBus(), newId = { "task-1" }),
            listId = "list-1",
            ownerAccountId = "acc-1",
            deviceId = "dev-1",
            scope = scope,
        )
    }

    @Test
    fun `a blank title cannot be saved`() {
        val viewModel = viewModel()
        assertFalse(viewModel.state.value.canSave)
    }

    @Test
    fun `saving a valid title creates the task and records its id for navigation`() =
        runTest {
            val repository = FakeTaskRepository()
            val viewModel = viewModel(repository)

            viewModel.dispatch(TaskCreateIntent.ChangeTitle("Buy milk"))
            viewModel.dispatch(TaskCreateIntent.Save)

            assertEquals("task-1", viewModel.state.value.createdTaskId)
            assertEquals("Buy milk", repository.tasks.getValue("task-1").title)
            assertFalse(viewModel.state.value.isSaving)
        }

    @Test
    fun `ConsumeCreated resets the form back to its initial empty state`() =
        runTest {
            val viewModel = viewModel()
            viewModel.dispatch(TaskCreateIntent.ChangeTitle("Buy milk"))
            viewModel.dispatch(TaskCreateIntent.Save)
            assertNotNull(viewModel.state.value.createdTaskId)

            viewModel.dispatch(TaskCreateIntent.ConsumeCreated)

            assertEquals(TaskCreateUiState(), viewModel.state.value)
        }

    @Test
    fun `a reminder is only attached when both due date and due time are set`() =
        runTest {
            val repository = FakeTaskRepository()
            val viewModel = viewModel(repository)
            viewModel.dispatch(TaskCreateIntent.ChangeTitle("Call the dentist"))
            viewModel.dispatch(TaskCreateIntent.ChangeDueDate(LocalDate(2026, 1, 2), null))
            viewModel.dispatch(TaskCreateIntent.ToggleReminder(true))

            viewModel.dispatch(TaskCreateIntent.Save)

            assertNull(repository.tasks.getValue("task-1").reminderConfig)
        }

    @Test
    fun `a reminder is attached when due date, due time, and the toggle are all set`() =
        runTest {
            val repository = FakeTaskRepository()
            val viewModel = viewModel(repository)
            viewModel.dispatch(TaskCreateIntent.ChangeTitle("Call the dentist"))
            viewModel.dispatch(TaskCreateIntent.ChangeDueDate(LocalDate(2026, 1, 2), LocalTime(9, 0)))
            viewModel.dispatch(TaskCreateIntent.ToggleReminder(true))

            viewModel.dispatch(TaskCreateIntent.Save)

            assertNotNull(repository.tasks.getValue("task-1").reminderConfig)
        }

    @Test
    fun `priority and notes are carried through to the created task`() =
        runTest {
            val repository = FakeTaskRepository()
            val viewModel = viewModel(repository)
            viewModel.dispatch(TaskCreateIntent.ChangeTitle("Call the dentist"))
            viewModel.dispatch(TaskCreateIntent.ChangePriority(TaskPriority.HIGH))
            viewModel.dispatch(TaskCreateIntent.ChangeNotes("Ask about the appointment on Friday"))

            viewModel.dispatch(TaskCreateIntent.Save)

            val created = repository.tasks.getValue("task-1")
            assertEquals(TaskPriority.HIGH, created.priority)
            assertEquals("Ask about the appointment on Friday", created.notes)
        }

    @Test
    fun `Save is a no-op while the title is blank, never calls CreateTask`() =
        runTest {
            val repository = FakeTaskRepository()
            val viewModel = viewModel(repository)

            viewModel.dispatch(TaskCreateIntent.Save)

            assertTrue(repository.tasks.isEmpty())
            assertNull(viewModel.state.value.createdTaskId)
        }
}
