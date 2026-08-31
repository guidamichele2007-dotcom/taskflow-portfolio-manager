package com.omnilife.domain.task.usecase

import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.eventbus.subscribe
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskPriority
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UpdateTaskFieldsTest {
    private fun repositoryWithTask(): FakeTaskRepository {
        val repository = FakeTaskRepository()
        repository.tasks["task-1"] = Task(envelope = testEnvelope("task-1"), title = "Original", listId = "list-1")
        return repository
    }

    @Test
    fun `an unchanged field keeps its previous value`() =
        runTest {
            val repository = repositoryWithTask()

            UpdateTaskFields(repository, InMemoryEventBus())(
                "task-1",
                TaskFieldEdits(priority = Edit.Set(TaskPriority.HIGH)),
            )

            assertEquals("Original", repository.tasks.getValue("task-1").title)
        }

    @Test
    fun `setting a field to null clears it, e-g removing a due date`() =
        runTest {
            val repository = repositoryWithTask()
            repository.tasks["task-1"] = repository.tasks.getValue("task-1").copy(dueDate = LocalDate(2026, 7, 21))

            UpdateTaskFields(repository, InMemoryEventBus())("task-1", TaskFieldEdits(dueDate = Edit.Set(null)))

            assertNull(repository.tasks.getValue("task-1").dueDate)
        }

    @Test
    fun `clearing the title to blank is rejected (TASK-R-01)`() =
        runTest {
            val repository = repositoryWithTask()

            val result =
                UpdateTaskFields(repository, InMemoryEventBus())("task-1", TaskFieldEdits(title = Edit.Set("   ")))

            assertEquals(TaskError.MissingTitle, (result as OmniResult.Failure).error)
            assertEquals("Original", repository.tasks.getValue("task-1").title)
        }

    @Test
    fun `updating an unknown task fails`() =
        runTest {
            val result =
                UpdateTaskFields(FakeTaskRepository(), InMemoryEventBus())(
                    "missing",
                    TaskFieldEdits(title = Edit.Set("x")),
                )
            assertEquals(TaskError.TaskNotFound("missing"), (result as OmniResult.Failure).error)
        }

    @Test
    fun `a successful edit publishes TaskEvent Updated so consumers like the search index can react`() =
        runTest {
            val repository = repositoryWithTask()
            val eventBus = InMemoryEventBus()
            val received = mutableListOf<TaskEvent.Updated>()
            eventBus.subscribe<TaskEvent.Updated> { received.add(it) }

            UpdateTaskFields(repository, eventBus)("task-1", TaskFieldEdits(title = Edit.Set("Renamed")))

            assertEquals(1, received.size)
            assertEquals("task-1", received.single().taskId)
        }

    @Test
    fun `a failed edit does not publish TaskEvent Updated`() =
        runTest {
            val repository = repositoryWithTask()
            val eventBus = InMemoryEventBus()
            var eventCount = 0
            eventBus.subscribe<TaskEvent.Updated> { eventCount++ }

            UpdateTaskFields(repository, eventBus)("task-1", TaskFieldEdits(title = Edit.Set("   ")))

            assertTrue(eventCount == 0)
        }
}
