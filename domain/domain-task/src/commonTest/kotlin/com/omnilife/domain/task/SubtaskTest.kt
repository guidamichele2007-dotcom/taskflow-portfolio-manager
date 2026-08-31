package com.omnilife.domain.task

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class SubtaskTest {
    @Test
    fun `title is mandatory`() {
        assertFailsWith<IllegalArgumentException> {
            Subtask(id = "sub-1", taskId = "task-1", title = "")
        }
    }

    @Test
    fun `a new subtask is not completed by default`() {
        val subtask = Subtask(id = "sub-1", taskId = "task-1", title = "Buy milk")
        assertFalse(subtask.completed)
    }
}
