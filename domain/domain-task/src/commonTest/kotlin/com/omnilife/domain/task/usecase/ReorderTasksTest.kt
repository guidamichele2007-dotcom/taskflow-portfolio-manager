package com.omnilife.domain.task.usecase

import com.omnilife.core.common.OmniResult
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReorderTasksTest {
    @Test
    fun `manual order always wins over the default sort (INV-10)`() = runTest {
        val repository = FakeTaskRepository().apply {
            tasks["a"] = Task(envelope = testEnvelope("a"), title = "a", listId = "list-1")
            tasks["b"] = Task(envelope = testEnvelope("b"), title = "b", listId = "list-1")
        }

        ReorderTasks(repository)(listOf("b", "a"))

        assertEquals(0, repository.tasks.getValue("b").manualOrder)
        assertEquals(1, repository.tasks.getValue("a").manualOrder)
    }

    @Test
    fun `reordering an unknown task id fails without partial writes to later ids`() = runTest {
        val repository = FakeTaskRepository().apply {
            tasks["a"] = Task(envelope = testEnvelope("a"), title = "a", listId = "list-1")
        }

        val result = ReorderTasks(repository)(listOf("a", "missing"))

        assertEquals(TaskError.TaskNotFound("missing"), (result as OmniResult.Failure).error)
        assertEquals(0, repository.tasks.getValue("a").manualOrder)
    }
}
