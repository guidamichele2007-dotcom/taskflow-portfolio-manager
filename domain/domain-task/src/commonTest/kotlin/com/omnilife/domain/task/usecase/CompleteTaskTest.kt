package com.omnilife.domain.task.usecase

import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.RecurrenceRule
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CompleteTaskTest {
    private fun repositoryWith(task: Task): FakeTaskRepository {
        val repository = FakeTaskRepository()
        repository.tasks[task.envelope.id] = task
        return repository
    }

    @Test
    fun `TASK-AC-01 completing a weekly recurring task generates exactly one next occurrence`() =
        runTest {
            // "every Monday" task due 2026-07-20 (a Monday), completed on time.
            val task =
                Task(
                    envelope = testEnvelope("task-1"),
                    title = "Weekly sync",
                    listId = "list-1",
                    dueDate = LocalDate(2026, 7, 20),
                    recurrenceRule = RecurrenceRule.Weekly(setOf(DayOfWeek.MONDAY)),
                )
            val repository = repositoryWith(task)
            val eventBus = InMemoryEventBus()
            var counter = 0
            val completeTask = CompleteTask(repository, eventBus, newId = { "task-${counter++}" })

            val result = completeTask("task-1")

            assertIs<OmniResult.Success<Task>>(result)
            val allTasks = repository.tasks.values
            assertEquals(2, allTasks.size, "the completed occurrence plus exactly one next occurrence")
            val nextOccurrence = allTasks.single { it.envelope.id != "task-1" }
            assertEquals(LocalDate(2026, 7, 27), nextOccurrence.dueDate)
            assertTrue(!nextOccurrence.completed)
        }

    @Test
    fun `completing a non-recurring task never creates a new occurrence`() =
        runTest {
            val task = Task(envelope = testEnvelope("task-1"), title = "One-off", listId = "list-1")
            val repository = repositoryWith(task)
            val completeTask = CompleteTask(repository, InMemoryEventBus(), newId = { "task-2" })

            completeTask("task-1")

            assertEquals(1, repository.tasks.size)
        }

    @Test
    fun `TASK-AC-03 completing a task with open subtasks requires an explicit choice`() =
        runTest {
            val task = Task(envelope = testEnvelope("task-1"), title = "With subtasks", listId = "list-1")
            val repository = repositoryWith(task)
            repository.subtasks["sub-1"] = Subtask(id = "sub-1", taskId = "task-1", title = "Step 1")
            val completeTask = CompleteTask(repository, InMemoryEventBus(), newId = { "task-2" })

            val result = completeTask("task-1", completeOpenSubtasks = null)

            assertEquals(TaskError.OpenSubtasksRequireChoice, (result as OmniResult.Failure).error)
            val message = "task must remain untouched until the choice is made"
            assertTrue(!repository.tasks.getValue("task-1").completed, message)
        }

    @Test
    fun `resolving the subtask choice with true completes every open subtask`() =
        runTest {
            val task = Task(envelope = testEnvelope("task-1"), title = "With subtasks", listId = "list-1")
            val repository = repositoryWith(task)
            repository.subtasks["sub-1"] = Subtask(id = "sub-1", taskId = "task-1", title = "Step 1")
            val completeTask = CompleteTask(repository, InMemoryEventBus(), newId = { "task-2" })

            completeTask("task-1", completeOpenSubtasks = true)

            assertTrue(repository.subtasks.getValue("sub-1").completed)
            assertTrue(repository.tasks.getValue("task-1").completed)
        }

    @Test
    fun `resolving the subtask choice with false completes the task but leaves subtasks open`() =
        runTest {
            val task = Task(envelope = testEnvelope("task-1"), title = "With subtasks", listId = "list-1")
            val repository = repositoryWith(task)
            repository.subtasks["sub-1"] = Subtask(id = "sub-1", taskId = "task-1", title = "Step 1")
            val completeTask = CompleteTask(repository, InMemoryEventBus(), newId = { "task-2" })

            completeTask("task-1", completeOpenSubtasks = false)

            assertTrue(!repository.subtasks.getValue("sub-1").completed)
            assertTrue(repository.tasks.getValue("task-1").completed)
        }

    @Test
    fun `completing an already-completed task is a no-op success`() =
        runTest {
            val task =
                Task(
                    envelope = testEnvelope("task-1"),
                    title = "Already done",
                    listId = "list-1",
                    completed = true,
                )
            val repository = repositoryWith(task)
            val completeTask = CompleteTask(repository, InMemoryEventBus(), newId = { "task-2" })

            val result = completeTask("task-1")

            assertIs<OmniResult.Success<Task>>(result)
            assertEquals(1, repository.tasks.size)
        }

    @Test
    fun `completing an unknown task returns TaskNotFound`() =
        runTest {
            val repository = FakeTaskRepository()
            val completeTask = CompleteTask(repository, InMemoryEventBus(), newId = { "task-2" })

            val result = completeTask("missing")

            assertEquals(TaskError.TaskNotFound("missing"), (result as OmniResult.Failure).error)
        }
}
