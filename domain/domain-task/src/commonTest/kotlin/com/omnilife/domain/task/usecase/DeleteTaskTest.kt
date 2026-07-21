package com.omnilife.domain.task.usecase

import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskError
import com.omnilife.domain.task.testEnvelope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeleteTaskTest {
    @Test
    fun `delete moves the task to trash without removing it (MFC-R-09)`() =
        runTest {
            val task = Task(envelope = testEnvelope("task-1"), title = "To delete", listId = "list-1")
            val repository = FakeTaskRepository().apply { tasks[task.envelope.id] = task }
            val deleteTask = DeleteTask(repository, InMemoryEventBus())

            deleteTask("task-1")

            val stored = repository.tasks.getValue("task-1")
            assertEquals(EntityLifecycleState.TRASHED, stored.envelope.lifecycleState)
            assertNotNull(stored.envelope.trashedAt)
        }

    @Test
    fun `restore returns a trashed task to active with no trashedAt`() =
        runTest {
            val task = Task(envelope = testEnvelope("task-1"), title = "Trashed", listId = "list-1")
            val repository = FakeTaskRepository().apply { tasks[task.envelope.id] = task }
            DeleteTask(repository, InMemoryEventBus())("task-1")

            val result = RestoreTask(repository)("task-1")

            val restored = (result as OmniResult.Success).value
            assertEquals(EntityLifecycleState.ACTIVE, restored.envelope.lifecycleState)
            assertNull(restored.envelope.trashedAt)
        }

    @Test
    fun `permanently deleting a task also removes its subtasks`() =
        runTest {
            val task = Task(envelope = testEnvelope("task-1"), title = "To purge", listId = "list-1")
            val repository =
                FakeTaskRepository().apply {
                    tasks[task.envelope.id] = task
                    subtasks["sub-1"] = Subtask(id = "sub-1", taskId = "task-1", title = "Step")
                }

            PermanentlyDeleteTask(repository)("task-1")

            assertEquals(null, repository.tasks["task-1"])
            assertEquals(emptyList(), repository.findSubtasks("task-1"))
        }

    @Test
    fun `deleting an unknown task returns TaskNotFound`() =
        runTest {
            val repository = FakeTaskRepository()
            val result = DeleteTask(repository, InMemoryEventBus())("missing")
            assertEquals(TaskError.TaskNotFound("missing"), (result as OmniResult.Failure).error)
        }
}
