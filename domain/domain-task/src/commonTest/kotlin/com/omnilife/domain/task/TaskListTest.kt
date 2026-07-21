package com.omnilife.domain.task

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TaskListTest {
    @Test
    fun `name is mandatory`() {
        assertFailsWith<IllegalArgumentException> {
            TaskList(envelope = testEnvelope(), name = "  ")
        }
    }

    @Test
    fun `area is optional, list has no area by default`() {
        val list = TaskList(envelope = testEnvelope(), name = "Attività")
        assertTrue(list.area == null)
    }

    @Test
    fun `the default list is flagged as such`() {
        val list = TaskList(envelope = testEnvelope(), name = "Attività", isDefault = true)
        assertTrue(list.isDefault)
    }
}
