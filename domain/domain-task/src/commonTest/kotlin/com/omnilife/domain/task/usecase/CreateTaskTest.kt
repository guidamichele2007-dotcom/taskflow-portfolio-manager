package com.omnilife.domain.task.usecase

import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.EventSubscriber
import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.TaskEvent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CreateTaskTest {
    @Test
    fun `creates a task with only a title and publishes task-item-created`() = runTest {
        val repository = FakeTaskRepository()
        val eventBus = InMemoryEventBus()
        val createdEvents = mutableListOf<TaskEvent.Created>()
        eventBus.subscribe(TaskEvent.Created::class, EventSubscriber { createdEvents.add(it) })
        var idCounter = 0
        val createTask = CreateTask(repository, eventBus, newId = { "task-${idCounter++}" })

        val result = createTask(
            title = "Call the accountant",
            listId = "list-1",
            ownerAccountId = "account-1",
            deviceId = "device-1",
        )

        val task = assertIs<OmniResult.Success<Task>>(result).value
        assertEquals("Call the accountant", task.title)
        assertEquals(1, repository.tasks.size)
        assertEquals(1, createdEvents.size)
        assertEquals(task.envelope.id, createdEvents.single().taskId)
    }

    @Test
    fun `rejects a blank title without touching the repository`() = runTest {
        val repository = FakeTaskRepository()
        val createTask = CreateTask(repository, InMemoryEventBus(), newId = { "task-1" })

        val result = createTask(title = "   ", listId = "list-1", ownerAccountId = "a", deviceId = "d")

        assertTrue(result is OmniResult.Failure)
        assertEquals(TaskError.MissingTitle, (result as OmniResult.Failure).error)
        assertTrue(repository.tasks.isEmpty())
    }
}
