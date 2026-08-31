package com.omnilife.feature.task.bridge

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.sync.InMemoryChangeTracker
import com.omnilife.core.sync.InMemorySyncOutboxStore
import com.omnilife.core.sync.InMemorySyncStateManager
import com.omnilife.domain.task.usecase.CompleteTask
import com.omnilife.domain.task.usecase.CreateTask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.Edit
import com.omnilife.domain.task.usecase.NewTaskDetails
import com.omnilife.domain.task.usecase.RestoreTask
import com.omnilife.domain.task.usecase.TaskFieldEdits
import com.omnilife.domain.task.usecase.UpdateTaskFields
import com.omnilife.feature.task.FakeTaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class SyncFixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskSyncOutboxBridgeTest {
    private val now = Instant.parse("2026-01-01T10:00:00Z")

    private fun newBridge(
        repository: FakeTaskRepository,
        eventBus: InMemoryEventBus,
        outboxStore: InMemorySyncOutboxStore,
        syncStateManager: InMemorySyncStateManager,
        scope: CoroutineScope,
    ) = TaskSyncOutboxBridge(
        repository = repository,
        outboxStore = outboxStore,
        changeTracker = InMemoryChangeTracker(),
        syncStateManager = syncStateManager,
        deviceId = "device-1",
        eventBus = eventBus,
        scope = scope,
        clock = SyncFixedClock(now),
    )

    @Test
    fun `creating a task enqueues exactly one outbox item`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val outboxStore = InMemorySyncOutboxStore()
            val syncStateManager = InMemorySyncStateManager()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, outboxStore, syncStateManager, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })

            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")

            assertEquals(1, outboxStore.size())
        }

    @Test
    fun `enqueueing updates SyncStateManager's pendingCount without asserting any sync outcome`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val outboxStore = InMemorySyncOutboxStore()
            val syncStateManager = InMemorySyncStateManager()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, outboxStore, syncStateManager, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })

            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")

            assertEquals(1, syncStateManager.current().pendingCount)
            assertEquals(com.omnilife.core.sync.SyncPhase.IDLE, syncStateManager.current().phase)
        }

    @Test
    fun `a task due today is enqueued as hot`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val outboxStore = InMemorySyncOutboxStore()
            val syncStateManager = InMemorySyncStateManager()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, outboxStore, syncStateManager, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = SyncFixedClock(now))

            createTask(
                "Pay rent",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details = NewTaskDetails(dueDate = LocalDate(2026, 1, 1)),
            )

            assertTrue(outboxStore.peekAll().single().isHot)
        }

    @Test
    fun `a task with no due date is enqueued as not hot`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val outboxStore = InMemorySyncOutboxStore()
            val syncStateManager = InMemorySyncStateManager()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, outboxStore, syncStateManager, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = SyncFixedClock(now))

            createTask("Someday maybe", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")

            assertFalse(outboxStore.peekAll().single().isHot)
        }

    @Test
    fun `editing then completing a task enqueues a separate item for each mutation`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val outboxStore = InMemorySyncOutboxStore()
            val syncStateManager = InMemorySyncStateManager()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, outboxStore, syncStateManager, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = SyncFixedClock(now))
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")
            val updateTaskFields = UpdateTaskFields(repository, eventBus, clock = SyncFixedClock(now))
            val completeTask = CompleteTask(repository, eventBus, newId = { "task-2" }, clock = SyncFixedClock(now))

            updateTaskFields("task-1", TaskFieldEdits(title = Edit.Set("Buy oat milk")))
            completeTask("task-1")

            assertEquals(3, outboxStore.size())
        }

    @Test
    fun `restoring a trashed task enqueues an outbox item too (Sprint 6 fix)`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val outboxStore = InMemorySyncOutboxStore()
            val syncStateManager = InMemorySyncStateManager()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, outboxStore, syncStateManager, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = SyncFixedClock(now))
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")
            val deleteTask = DeleteTask(repository, eventBus, clock = SyncFixedClock(now))
            deleteTask("task-1")
            val restoreTask = RestoreTask(repository, eventBus, clock = SyncFixedClock(now))

            restoreTask("task-1")

            assertEquals(3, outboxStore.size())
        }

    @Test
    fun `after clear, further task events no longer enqueue anything`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val outboxStore = InMemorySyncOutboxStore()
            val syncStateManager = InMemorySyncStateManager()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            val bridge = newBridge(repository, eventBus, outboxStore, syncStateManager, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })

            bridge.clear()
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")

            assertEquals(0, outboxStore.size())
        }
}
