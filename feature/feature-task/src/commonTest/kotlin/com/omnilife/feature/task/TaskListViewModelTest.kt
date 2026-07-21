package com.omnilife.feature.task

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.usecase.CompleteTask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.GetTasksForView
import com.omnilife.domain.task.usecase.PostponeTask
import com.omnilife.domain.task.usecase.PostponeTarget
import com.omnilife.domain.task.usecase.ReorderTasks
import com.omnilife.domain.task.usecase.SearchTasks
import com.omnilife.domain.task.usecase.TaskListMode
import com.omnilife.domain.task.usecase.UncompleteTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}

class TaskListViewModelTest {
    // 2026-07-21 (Tuesday).
    private val clock = FixedClock(Instant.parse("2026-07-21T09:00:00Z"))

    private fun task(id: String, dueDate: LocalDate?) = Task(
        envelope = testEnvelopeFixture(id),
        title = id,
        listId = "list-1",
        dueDate = dueDate,
    )

    private fun viewModel(repository: FakeTaskRepository, scope: CoroutineScope): TaskListViewModel {
        val eventBus = InMemoryEventBus()
        return TaskListViewModel(
            getTasksForView = GetTasksForView(repository, clock, TimeZone.UTC),
            completeTask = CompleteTask(repository, eventBus, newId = { "generated" }, clock = clock),
            uncompleteTask = UncompleteTask(repository, eventBus, clock),
            deleteTask = DeleteTask(repository, eventBus, clock),
            postponeTask = PostponeTask(repository, eventBus, clock, TimeZone.UTC),
            reorderTasks = ReorderTasks(repository, clock),
            searchTasks = SearchTasks(repository),
            scope = scope,
        )
    }

    @Test
    fun `loads the today view on creation`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = FakeTaskRepository().apply {
            tasks["today"] = task("today", LocalDate(2026, 7, 21))
            tasks["future"] = task("future", LocalDate(2026, 8, 1))
        }

        val viewModel = viewModel(repository, scope)

        assertEquals(listOf("today"), viewModel.state.value.tasks.map { it.title })
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `changing mode reloads the list for the new view`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = FakeTaskRepository().apply {
            tasks["future"] = task("future", LocalDate(2026, 8, 1))
        }
        val viewModel = viewModel(repository, scope)

        viewModel.dispatch(TaskListIntent.ChangeMode(TaskListMode.UPCOMING))

        assertEquals(TaskListMode.UPCOMING, viewModel.state.value.mode)
        assertEquals(listOf("future"), viewModel.state.value.tasks.map { it.title })
    }

    @Test
    fun `completing a task with open subtasks surfaces a pending choice instead of failing silently`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = FakeTaskRepository().apply {
            tasks["today"] = task("today", LocalDate(2026, 7, 21))
            subtasks["sub-1"] = Subtask(id = "sub-1", taskId = "today", title = "Step")
        }
        val viewModel = viewModel(repository, scope)

        viewModel.dispatch(TaskListIntent.Complete("today"))

        assertEquals("today", viewModel.state.value.pendingSubtaskChoiceForTaskId)
        assertTrue(!repository.tasks.getValue("today").completed)
    }

    @Test
    fun `resolving the pending subtask choice completes the task and clears the pending state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = FakeTaskRepository().apply {
            tasks["today"] = task("today", LocalDate(2026, 7, 21))
            subtasks["sub-1"] = Subtask(id = "sub-1", taskId = "today", title = "Step")
        }
        val viewModel = viewModel(repository, scope)
        viewModel.dispatch(TaskListIntent.Complete("today"))

        viewModel.dispatch(TaskListIntent.ResolveSubtaskChoice("today", completeOpenSubtasks = true))

        assertEquals(null, viewModel.state.value.pendingSubtaskChoiceForTaskId)
        assertTrue(repository.tasks.getValue("today").completed)
    }

    @Test
    fun `search updates the query and the visible tasks`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = FakeTaskRepository().apply {
            tasks["a"] = Task(envelope = testEnvelopeFixture("a"), title = "Call the accountant", listId = "list-1")
            tasks["b"] = Task(envelope = testEnvelopeFixture("b"), title = "Buy milk", listId = "list-1")
        }
        val viewModel = viewModel(repository, scope)

        viewModel.dispatch(TaskListIntent.Search("accountant"))

        assertEquals("accountant", viewModel.state.value.searchQuery)
        assertEquals(listOf("a"), viewModel.state.value.tasks.map { it.envelope.id })
    }

    @Test
    fun `clearing the search query restores the current view`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = FakeTaskRepository().apply {
            tasks["today"] = task("today", LocalDate(2026, 7, 21))
        }
        val viewModel = viewModel(repository, scope)
        viewModel.dispatch(TaskListIntent.Search("today"))

        viewModel.dispatch(TaskListIntent.Search(""))

        assertEquals(listOf("today"), viewModel.state.value.tasks.map { it.title })
    }

    @Test
    fun `postpone reschedules and refreshes the view`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val repository = FakeTaskRepository().apply {
            tasks["today"] = task("today", LocalDate(2026, 7, 21))
        }
        val viewModel = viewModel(repository, scope)

        viewModel.dispatch(TaskListIntent.Postpone("today", PostponeTarget.Tomorrow))

        assertEquals(LocalDate(2026, 7, 22), repository.tasks.getValue("today").dueDate)
        // Tomorrow's date no longer belongs to the Today view.
        assertTrue(viewModel.state.value.tasks.none { it.envelope.id == "today" })
    }

    @Test
    fun `an unknown task error is surfaced on the state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher())
        val viewModel = viewModel(FakeTaskRepository(), scope)

        viewModel.dispatch(TaskListIntent.Complete("missing"))

        assertEquals(TaskError.TaskNotFound("missing").message, viewModel.state.value.errorMessage)
    }
}
