package com.omnilife.domain.task

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskTest {
    @Test
    fun `title is the only mandatory field`() {
        val task = Task(envelope = testEnvelope(), title = "Call the accountant", listId = "list-1")
        assertTrue(task.title.isNotBlank())
    }

    @Test
    fun `blank title is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            Task(envelope = testEnvelope(), title = "   ", listId = "list-1")
        }
    }

    @Test
    fun `a task with no due date is never overdue`() {
        val task = Task(envelope = testEnvelope(), title = "Someday", listId = "list-1", dueDate = null)
        assertFalse(task.isOverdue(LocalDate(2026, 7, 21)))
    }

    @Test
    fun `a task due before today and not completed is overdue`() {
        val task =
            Task(
                envelope = testEnvelope(),
                title = "Overdue",
                listId = "list-1",
                dueDate = LocalDate(2026, 7, 19),
            )
        assertTrue(task.isOverdue(LocalDate(2026, 7, 21)))
    }

    @Test
    fun `a completed task is never overdue even if its due date has passed`() {
        val task =
            Task(
                envelope = testEnvelope(),
                title = "Done already",
                listId = "list-1",
                dueDate = LocalDate(2026, 7, 19),
                completed = true,
            )
        assertFalse(task.isOverdue(LocalDate(2026, 7, 21)))
    }

    @Test
    fun `a task due today is not overdue`() {
        val task =
            Task(
                envelope = testEnvelope(),
                title = "Due today",
                listId = "list-1",
                dueDate = LocalDate(2026, 7, 21),
            )
        assertFalse(task.isOverdue(LocalDate(2026, 7, 21)))
    }
}
