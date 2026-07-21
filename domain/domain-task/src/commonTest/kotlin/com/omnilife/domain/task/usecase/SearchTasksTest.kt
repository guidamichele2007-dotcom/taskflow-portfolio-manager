package com.omnilife.domain.task.usecase

import com.omnilife.domain.task.Task
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchTasksTest {
    @Test
    fun `finds tasks whose title matches the query`() =
        runTest {
            val repository =
                FakeTaskRepository().apply {
                    tasks["a"] = Task(envelope = testEnvelope("a"), title = "Call the accountant", listId = "list-1")
                    tasks["b"] = Task(envelope = testEnvelope("b"), title = "Buy milk", listId = "list-1")
                }

            val results = SearchTasks(repository)("accountant")

            assertEquals(listOf("a"), results.map { it.envelope.id })
        }

    @Test
    fun `finds tasks whose notes match the query`() =
        runTest {
            val repository =
                FakeTaskRepository().apply {
                    tasks["a"] = Task(envelope = testEnvelope("a"), title = "T", listId = "list-1", notes = "ask about invoice")
                }

            val results = SearchTasks(repository)("invoice")

            assertEquals(1, results.size)
        }

    @Test
    fun `a blank query returns no results rather than everything`() =
        runTest {
            val repository =
                FakeTaskRepository().apply {
                    tasks["a"] = Task(envelope = testEnvelope("a"), title = "T", listId = "list-1")
                }

            assertTrue(SearchTasks(repository)("   ").isEmpty())
        }
}
