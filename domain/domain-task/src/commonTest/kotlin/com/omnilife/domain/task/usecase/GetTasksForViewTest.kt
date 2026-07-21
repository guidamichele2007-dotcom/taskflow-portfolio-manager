package com.omnilife.domain.task.usecase

import com.omnilife.domain.task.FixedClock
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTasksForViewTest {
    // 2026-07-21 (Tuesday) as "today".
    private val clock = FixedClock(Instant.parse("2026-07-21T09:00:00Z"))
    private val utc = TimeZone.UTC

    private fun task(id: String, dueDate: LocalDate?, completed: Boolean = false) = Task(
        envelope = testEnvelope(id),
        title = id,
        listId = "list-1",
        dueDate = dueDate,
        completed = completed,
    )

    @Test
    fun `TASK-014 overdue and today are never mixed`() = runTest {
        val repository = FakeTaskRepository().apply {
            tasks["overdue"] = task("overdue", LocalDate(2026, 7, 19))
            tasks["today"] = task("today", LocalDate(2026, 7, 21))
        }
        val getTasksForView = GetTasksForView(repository, clock, utc)

        val todayView = getTasksForView(TaskListMode.TODAY)
        val overdueView = getTasksForView(TaskListMode.OVERDUE)

        assertEquals(listOf("today"), todayView.map { it.title })
        assertEquals(listOf("overdue"), overdueView.map { it.title })
    }

    @Test
    fun `upcoming view only shows tasks due strictly after today`() = runTest {
        val repository = FakeTaskRepository().apply {
            tasks["today"] = task("today", LocalDate(2026, 7, 21))
            tasks["future"] = task("future", LocalDate(2026, 7, 25))
        }
        val getTasksForView = GetTasksForView(repository, clock, utc)

        val upcoming = getTasksForView(TaskListMode.UPCOMING)

        assertEquals(listOf("future"), upcoming.map { it.title })
    }

    @Test
    fun `a completed overdue task never appears in the overdue view`() = runTest {
        val repository = FakeTaskRepository().apply {
            tasks["done"] = task("done", LocalDate(2026, 7, 19), completed = true)
        }
        val getTasksForView = GetTasksForView(repository, clock, utc)

        assertTrue(getTasksForView(TaskListMode.OVERDUE).isEmpty())
    }

    @Test
    fun `all view returns every active task regardless of date`() = runTest {
        val repository = FakeTaskRepository().apply {
            tasks["a"] = task("a", null)
            tasks["b"] = task("b", LocalDate(2026, 7, 19))
            tasks["c"] = task("c", LocalDate(2026, 8, 1))
        }
        val getTasksForView = GetTasksForView(repository, clock, utc)

        assertEquals(3, getTasksForView(TaskListMode.ALL).size)
    }
}
