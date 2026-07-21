package com.omnilife.domain.task.usecase

import com.omnilife.core.common.OmniResult
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SubtaskUseCasesTest {
    private fun repositoryWithTask(): FakeTaskRepository {
        val repository = FakeTaskRepository()
        repository.tasks["task-1"] = Task(envelope = testEnvelope("task-1"), title = "Parent", listId = "list-1")
        return repository
    }

    @Test
    fun `add subtask appends it with the next order index`() = runTest {
        val repository = repositoryWithTask()
        val addSubtask = AddSubtask(repository, newId = { "sub-1" })

        addSubtask("task-1", "Step 1")
        val second = AddSubtask(repository, newId = { "sub-2" })("task-1", "Step 2")

        assertEquals(1, (second as OmniResult.Success).value.order)
    }

    @Test
    fun `add subtask rejects a blank title`() = runTest {
        val repository = repositoryWithTask()
        val result = AddSubtask(repository, newId = { "sub-1" })("task-1", "  ")
        assertEquals(TaskError.MissingTitle, (result as OmniResult.Failure).error)
    }

    @Test
    fun `add subtask to an unknown task fails`() = runTest {
        val result = AddSubtask(FakeTaskRepository(), newId = { "sub-1" })("missing", "Step")
        assertEquals(TaskError.TaskNotFound("missing"), (result as OmniResult.Failure).error)
    }

    @Test
    fun `toggle subtask flips its completed flag`() = runTest {
        val repository = repositoryWithTask()
        AddSubtask(repository, newId = { "sub-1" })("task-1", "Step 1")

        val toggled = ToggleSubtask(repository)("sub-1", "task-1")
        assertTrue((toggled as OmniResult.Success).value.completed)

        val toggledBack = ToggleSubtask(repository)("sub-1", "task-1")
        assertTrue(!(toggledBack as OmniResult.Success).value.completed)
    }

    @Test
    fun `delete subtask removes it`() = runTest {
        val repository = repositoryWithTask()
        AddSubtask(repository, newId = { "sub-1" })("task-1", "Step 1")

        DeleteSubtask(repository)("sub-1")

        assertTrue(repository.findSubtasks("task-1").isEmpty())
    }

    @Test
    fun `reorder subtasks applies the new order`() = runTest {
        val repository = repositoryWithTask()
        AddSubtask(repository, newId = { "sub-1" })("task-1", "First")
        AddSubtask(repository, newId = { "sub-2" })("task-1", "Second")

        val result = ReorderSubtasks(repository)("task-1", listOf("sub-2", "sub-1"))

        assertIs<OmniResult.Success<Unit>>(result)
        val ordered = repository.findSubtasks("task-1")
        assertEquals(listOf("sub-2", "sub-1"), ordered.map { it.id })
    }
}
