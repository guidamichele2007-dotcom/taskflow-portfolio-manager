package com.omnilife.feature.task.bridge

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.search.SearchService
import com.omnilife.domain.task.persistence.DatabaseDriverFactory
import com.omnilife.domain.task.persistence.SqlDelightTaskRepository
import com.omnilife.domain.task.usecase.CreateTask
import com.omnilife.domain.task.usecase.DeleteTask
import com.omnilife.domain.task.usecase.NewTaskDetails
import com.omnilife.domain.task.usecase.PermanentlyDeleteTask
import com.omnilife.domain.task.usecase.RestoreTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * MVP Release 1.0 (Fase 5, "elimina qualsiasi ghost result"): unlike `TaskSearchIndexBridgeTest`
 * (which uses a `FakeSearchIndexer` to isolate the bridge's own wiring logic), this exercises the
 * real `SqlDelightSearchIndex` (FTS5, JVM in-memory SQLite) and the real
 * `SqlDelightTaskRepository` together — no fakes at all — to prove end to end that a task's
 * visibility in default search results tracks its real lifecycle, with nothing left behind.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TaskSearchGhostResultIntegrationTest {
    private fun titlesOf(results: List<com.omnilife.core.search.SearchResult>) = results.map { it.title }

    @Test
    fun `a task's search visibility tracks create, trash, restore, and permanent delete with no ghosts left behind`() =
        runTest {
            val repository = SqlDelightTaskRepository(DatabaseDriverFactory().createDriver())
            val searchService = SearchService(com.omnilife.core.search.persistence.DatabaseDriverFactory())
            val eventBus = InMemoryEventBus()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            TaskSearchIndexBridge(repository, searchService.indexer, eventBus, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "ghost-task-1" })
            val deleteTask = DeleteTask(repository, eventBus)
            val restoreTask = RestoreTask(repository, eventBus)
            val permanentlyDeleteTask = PermanentlyDeleteTask(repository, eventBus)

            createTask(
                "Renew passport",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details = NewTaskDetails(dueDate = LocalDate(2026, 6, 1)),
            )
            assertEquals(listOf("Renew passport"), titlesOf(searchService.search.search("passport")))

            // Trashing must hide it from default results without deleting the index row (SRCH-006).
            deleteTask("ghost-task-1")
            assertTrue(searchService.search.search("passport").isEmpty())

            // Restoring must make it findable again — the bug this release's TaskEvent.Restored
            // fix closed; before it, the index row stayed stuck at lifecycleState=TRASHED forever.
            restoreTask("ghost-task-1")
            assertEquals(listOf("Renew passport"), titlesOf(searchService.search.search("passport")))

            // Permanent deletion must remove the row entirely — not just hide it — so it can never
            // resurface as a ghost result under any filter.
            deleteTask("ghost-task-1")
            permanentlyDeleteTask("ghost-task-1")
            assertTrue(searchService.search.search("passport").isEmpty())
            assertEquals(0L, searchService.indexer.count())
        }
}
