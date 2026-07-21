package com.omnilife.domain.task.usecase

import com.omnilife.core.common.OmniResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CreateTaskListTest {
    @Test
    fun `creates a list with the given name`() = runTest {
        val repository = FakeTaskRepository()
        val createTaskList = CreateTaskList(repository, newId = { "list-1" })

        val result = createTaskList(name = "Casa", ownerAccountId = "account-1", deviceId = "device-1")

        val list = assertIs<OmniResult.Success<_>>(result).value
        assertEquals("Casa", list.name)
        assertEquals(1, repository.lists.size)
    }
}
