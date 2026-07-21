package com.omnilife.domain.task.usecase

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.FixedClock
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class PostponeTaskTest {
    private val utc = TimeZone.UTC

    // 2026-07-21T10:00:00Z is a Tuesday.
    private val fixedClock = FixedClock(Instant.parse("2026-07-21T10:00:00Z"))

    @Test
    fun `postpone to tomorrow moves the due date by exactly one day`() = runTest {
        val task = Task(envelope = testEnvelope("task-1"), title = "T", listId = "list-1")
        val repository = FakeTaskRepository().apply { tasks[task.envelope.id] = task }
        val postponeTask = PostponeTask(repository, InMemoryEventBus(), fixedClock, utc)

        postponeTask("task-1", PostponeTarget.Tomorrow)

        assertEquals(LocalDate(2026, 7, 22), repository.tasks.getValue("task-1").dueDate)
    }

    @Test
    fun `postpone to tonight sets the due date to today`() = runTest {
        val task = Task(envelope = testEnvelope("task-1"), title = "T", listId = "list-1")
        val repository = FakeTaskRepository().apply { tasks[task.envelope.id] = task }
        val postponeTask = PostponeTask(repository, InMemoryEventBus(), fixedClock, utc)

        postponeTask("task-1", PostponeTarget.Tonight)

        assertEquals(LocalDate(2026, 7, 21), repository.tasks.getValue("task-1").dueDate)
    }

    @Test
    fun `postpone to weekend lands on the next Saturday`() = runTest {
        val task = Task(envelope = testEnvelope("task-1"), title = "T", listId = "list-1")
        val repository = FakeTaskRepository().apply { tasks[task.envelope.id] = task }
        val postponeTask = PostponeTask(repository, InMemoryEventBus(), fixedClock, utc)

        postponeTask("task-1", PostponeTarget.Weekend)

        assertEquals(LocalDate(2026, 7, 25), repository.tasks.getValue("task-1").dueDate)
    }

    @Test
    fun `postpone to a specific date sets it verbatim`() = runTest {
        val task = Task(envelope = testEnvelope("task-1"), title = "T", listId = "list-1")
        val repository = FakeTaskRepository().apply { tasks[task.envelope.id] = task }
        val postponeTask = PostponeTask(repository, InMemoryEventBus(), fixedClock, utc)

        postponeTask("task-1", PostponeTarget.SpecificDate(LocalDate(2027, 1, 1)))

        assertEquals(LocalDate(2027, 1, 1), repository.tasks.getValue("task-1").dueDate)
    }
}
