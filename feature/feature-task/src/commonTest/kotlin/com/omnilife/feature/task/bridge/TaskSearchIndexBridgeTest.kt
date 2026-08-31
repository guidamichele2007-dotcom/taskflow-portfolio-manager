package com.omnilife.feature.task.bridge

import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.search.IndexableEntity
import com.omnilife.core.search.SearchIndexer
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.usecase.CreateTask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.Edit
import com.omnilife.domain.task.usecase.PermanentlyDeleteTask
import com.omnilife.domain.task.usecase.RestoreTask
import com.omnilife.domain.task.usecase.TaskFieldEdits
import com.omnilife.domain.task.usecase.UpdateTaskFields
import com.omnilife.feature.task.FakeTaskRepository
import com.omnilife.feature.task.testEnvelopeFixture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeSearchIndexer : SearchIndexer {
    val indexed = mutableMapOf<String, IndexableEntity>()

    override fun index(entity: IndexableEntity) {
        indexed[entity.id] = entity
    }

    override fun remove(entityId: String) {
        indexed.remove(entityId)
    }

    override fun rebuild(entities: List<IndexableEntity>) {
        indexed.clear()
        entities.forEach { indexed[it.id] = it }
    }

    override fun count(): Long = indexed.size.toLong()
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskSearchIndexBridgeTest {
    @Test
    fun `creating a task indexes it with its real title`() =
        runTest {
            val repository = FakeTaskRepository()
            val indexer = FakeSearchIndexer()
            val eventBus = InMemoryEventBus()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            TaskSearchIndexBridge(repository, indexer, eventBus, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })

            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")

            assertEquals("Buy milk", indexer.indexed["task-1"]?.title)
            assertEquals("ACTIVE", indexer.indexed["task-1"]?.lifecycleState)
        }

    @Test
    fun `editing a task's title updates the indexed title, not just re-adds the old one`() =
        runTest {
            val repository = FakeTaskRepository()
            val indexer = FakeSearchIndexer()
            val eventBus = InMemoryEventBus()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            TaskSearchIndexBridge(repository, indexer, eventBus, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")
            val updateTaskFields = UpdateTaskFields(repository, eventBus)

            updateTaskFields("task-1", TaskFieldEdits(title = Edit.Set("Buy oat milk")))

            assertEquals("Buy oat milk", indexer.indexed["task-1"]?.title)
            assertEquals(1, indexer.indexed.size)
        }

    @Test
    fun `deleting (trashing) a task re-indexes it as TRASHED, never removes it from the index`() =
        runTest {
            val repository = FakeTaskRepository()
            val indexer = FakeSearchIndexer()
            val eventBus = InMemoryEventBus()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            TaskSearchIndexBridge(repository, indexer, eventBus, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")
            val deleteTask = DeleteTask(repository, eventBus)

            deleteTask("task-1")

            assertEquals("TRASHED", indexer.indexed["task-1"]?.lifecycleState)
        }

    @Test
    fun `restoring a trashed task re-indexes it as ACTIVE (Sprint 6 fix)`() =
        runTest {
            val repository = FakeTaskRepository()
            val indexer = FakeSearchIndexer()
            val eventBus = InMemoryEventBus()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            TaskSearchIndexBridge(repository, indexer, eventBus, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")
            DeleteTask(repository, eventBus)("task-1")
            assertEquals("TRASHED", indexer.indexed["task-1"]?.lifecycleState)

            RestoreTask(repository, eventBus)("task-1")

            assertEquals("ACTIVE", indexer.indexed["task-1"]?.lifecycleState)
        }

    @Test
    fun `permanently deleting a task removes it from the index entirely (Sprint 6 fix)`() =
        runTest {
            val repository = FakeTaskRepository()
            val indexer = FakeSearchIndexer()
            val eventBus = InMemoryEventBus()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            TaskSearchIndexBridge(repository, indexer, eventBus, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")
            assertTrue(indexer.indexed.containsKey("task-1"))

            PermanentlyDeleteTask(repository, eventBus)("task-1")

            assertTrue(!indexer.indexed.containsKey("task-1"))
        }

    @Test
    fun `rebuildIndex includes both active and trashed tasks`() =
        runTest {
            val repository = FakeTaskRepository()
            val indexer = FakeSearchIndexer()
            val eventBus = InMemoryEventBus()
            repository.tasks["active-1"] =
                Task(envelope = testEnvelopeFixture("active-1"), title = "Active task", listId = "list-1")
            repository.tasks["trashed-1"] =
                Task(
                    envelope = testEnvelopeFixture("trashed-1").copy(lifecycleState = EntityLifecycleState.TRASHED),
                    title = "Trashed task",
                    listId = "list-1",
                )
            val bridge = TaskSearchIndexBridge(repository, indexer, eventBus)

            bridge.rebuildIndex()

            assertEquals(setOf("active-1", "trashed-1"), indexer.indexed.keys)
        }

    @Test
    fun `after clear, further task events no longer reach the indexer`() =
        runTest {
            val repository = FakeTaskRepository()
            val indexer = FakeSearchIndexer()
            val eventBus = InMemoryEventBus()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            val bridge = TaskSearchIndexBridge(repository, indexer, eventBus, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" })

            bridge.clear()
            createTask("Buy milk", listId = "list-1", ownerAccountId = "acc-1", deviceId = "dev-1")

            assertTrue(indexer.indexed.isEmpty())
        }
}
