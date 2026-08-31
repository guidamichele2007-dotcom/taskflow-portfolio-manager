package com.omnilife.domain.task.persistence

import com.omnilife.core.common.EntityLifecycleState
import com.omnilife.core.common.Envelope
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskFilter
import com.omnilife.domain.task.TaskList
import com.omnilife.domain.task.TaskPriority
import com.omnilife.domain.task.TaskSort
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Integration test (Engineering Plan T6 / IT): exercises the real
 * SQLDelight schema against an in-memory JVM SQLite database — the one
 * platform this sandbox can actually run (see README-BUILD.md §4).
 */
class SqlDelightTaskRepositoryTest {
    private lateinit var repository: SqlDelightTaskRepository

    private fun envelope(id: String) =
        Envelope(
            id = id,
            ownerAccountId = "account-1",
            schemaVersion = 1,
            createdAt = Instant.fromEpochMilliseconds(0),
            createdByDevice = "device-1",
            modifiedAt = Instant.fromEpochMilliseconds(0),
            modifiedByDevice = "device-1",
        )

    @BeforeTest
    fun setUp() {
        repository = SqlDelightTaskRepository(DatabaseDriverFactory().createDriver())
    }

    @Test
    fun `a task round-trips through insert and findById unchanged`() =
        runTest {
            val task =
                Task(
                    envelope = envelope("task-1"),
                    title = "Call the accountant",
                    listId = "list-1",
                    priority = TaskPriority.HIGH,
                )

            repository.insertTask(task)
            val found = repository.findTaskById("task-1")

            assertEquals(task, found)
        }

    @Test
    fun `updating a task overwrites the stored row`() =
        runTest {
            val task = Task(envelope = envelope("task-1"), title = "Original", listId = "list-1")
            repository.insertTask(task)

            repository.updateTask(task.copy(title = "Renamed"))

            assertEquals("Renamed", repository.findTaskById("task-1")?.title)
        }

    @Test
    fun `findTasks filters by lifecycle state`() =
        runTest {
            repository.insertTask(Task(envelope = envelope("active"), title = "Active", listId = "list-1"))
            repository.insertTask(
                Task(
                    envelope = envelope("trashed").copy(lifecycleState = EntityLifecycleState.TRASHED),
                    title = "Trashed",
                    listId = "list-1",
                ),
            )

            val activeOnly = repository.findTasks(TaskFilter(lifecycleState = EntityLifecycleState.ACTIVE))

            assertEquals(listOf("active"), activeOnly.map { it.envelope.id })
        }

    @Test
    fun `findTasks filters by list id`() =
        runTest {
            repository.insertTask(Task(envelope = envelope("a"), title = "A", listId = "list-1"))
            repository.insertTask(Task(envelope = envelope("b"), title = "B", listId = "list-2"))

            val list1Tasks = repository.findTasks(TaskFilter(listId = "list-1"))

            assertEquals(listOf("a"), list1Tasks.map { it.envelope.id })
        }

    @Test
    fun `findTasks combines list id and priority instead of dropping one`() =
        runTest {
            repository.insertTask(
                Task(envelope = envelope("match"), title = "Match", listId = "list-1", priority = TaskPriority.HIGH),
            )
            repository.insertTask(
                // Same list, wrong priority - the SQL branch picked for listId alone would
                // wrongly include this if the priority filter were dropped.
                Task(
                    envelope = envelope("wrong-priority"),
                    title = "X",
                    listId = "list-1",
                    priority = TaskPriority.NONE,
                ),
            )
            repository.insertTask(
                Task(envelope = envelope("wrong-list"), title = "Y", listId = "list-2", priority = TaskPriority.HIGH),
            )

            val results = repository.findTasks(TaskFilter(listId = "list-1", priority = TaskPriority.HIGH))

            assertEquals(listOf("match"), results.map { it.envelope.id })
        }

    @Test
    fun `searchTasks matches on title and notes, case-insensitively`() =
        runTest {
            repository.insertTask(Task(envelope = envelope("a"), title = "Call the Accountant", listId = "list-1"))
            repository.insertTask(
                Task(envelope = envelope("b"), title = "Unrelated", listId = "list-1", notes = "ask about INVOICE"),
            )
            repository.insertTask(Task(envelope = envelope("c"), title = "Buy milk", listId = "list-1"))

            val byTitle = repository.searchTasks("accountant", EntityLifecycleState.ACTIVE)
            val byNotes = repository.searchTasks("invoice", EntityLifecycleState.ACTIVE)

            assertEquals(listOf("a"), byTitle.map { it.envelope.id })
            assertEquals(listOf("b"), byNotes.map { it.envelope.id })
        }

    @Test
    fun `permanentlyDeleteTask removes the row entirely`() =
        runTest {
            repository.insertTask(Task(envelope = envelope("a"), title = "A", listId = "list-1"))

            repository.permanentlyDeleteTask("a")

            assertNull(repository.findTaskById("a"))
        }

    @Test
    fun `subtasks round-trip and are ordered`() =
        runTest {
            repository.insertTask(Task(envelope = envelope("task-1"), title = "Parent", listId = "list-1"))
            repository.insertSubtask(Subtask(id = "sub-2", taskId = "task-1", title = "Second", order = 1))
            repository.insertSubtask(Subtask(id = "sub-1", taskId = "task-1", title = "First", order = 0))

            val subtasks = repository.findSubtasks("task-1")

            assertEquals(listOf("sub-1", "sub-2"), subtasks.map { it.id })
        }

    @Test
    fun `deleting a task's subtasks removes only that task's rows`() =
        runTest {
            repository.insertTask(Task(envelope = envelope("task-1"), title = "P1", listId = "list-1"))
            repository.insertTask(Task(envelope = envelope("task-2"), title = "P2", listId = "list-1"))
            repository.insertSubtask(Subtask(id = "sub-1", taskId = "task-1", title = "A"))
            repository.insertSubtask(Subtask(id = "sub-2", taskId = "task-2", title = "B"))

            repository.permanentlyDeleteSubtasksForTask("task-1")

            assertTrue(repository.findSubtasks("task-1").isEmpty())
            assertEquals(1, repository.findSubtasks("task-2").size)
        }

    @Test
    fun `task lists round-trip through insert and findById`() =
        runTest {
            val list = TaskList(envelope = envelope("list-1"), name = "Casa", area = "Personale", isDefault = true)

            repository.insertList(list)

            assertEquals(list, repository.findListById("list-1"))
            assertEquals(listOf(list), repository.findAllLists())
        }

    @Test
    fun `TaskSort DUE_DATE orders by due date, undated tasks last`() =
        runTest {
            repository.insertTask(Task(envelope = envelope("no-date"), title = "No date", listId = "list-1"))
            repository.insertTask(
                Task(envelope = envelope("later"), title = "Later", listId = "list-1", dueDate = LocalDate(2026, 8, 1)),
            )
            repository.insertTask(
                Task(
                    envelope = envelope("sooner"),
                    title = "Sooner",
                    listId = "list-1",
                    dueDate = LocalDate(2026, 7, 21),
                ),
            )

            val ordered = repository.findTasks(sort = TaskSort.DUE_DATE)

            assertEquals(listOf("sooner", "later", "no-date"), ordered.map { it.envelope.id })
        }

    @Test
    fun `TaskSort PRIORITY orders highest priority first`() =
        runTest {
            repository.insertTask(
                Task(envelope = envelope("low"), title = "Low", listId = "list-1", priority = TaskPriority.NONE),
            )
            repository.insertTask(
                Task(envelope = envelope("high"), title = "High", listId = "list-1", priority = TaskPriority.HIGH),
            )
            repository.insertTask(
                Task(
                    envelope = envelope("medium"),
                    title = "Medium",
                    listId = "list-1",
                    priority = TaskPriority.MEDIUM,
                ),
            )

            val ordered = repository.findTasks(sort = TaskSort.PRIORITY)

            assertEquals(listOf("high", "medium", "low"), ordered.map { it.envelope.id })
        }

    @Test
    fun `TaskSort DEFAULT lets manual order win over due date and priority (INV-10)`() =
        runTest {
            repository.insertTask(
                Task(
                    envelope = envelope("urgent-no-order"),
                    title = "Urgent",
                    listId = "list-1",
                    priority = TaskPriority.HIGH,
                ),
            )
            repository.insertTask(
                Task(
                    envelope = envelope("manually-first"),
                    title = "Manually first",
                    listId = "list-1",
                    priority = TaskPriority.NONE,
                    manualOrder = 0,
                ),
            )

            val ordered = repository.findTasks(sort = TaskSort.DEFAULT)

            assertEquals(listOf("manually-first", "urgent-no-order"), ordered.map { it.envelope.id })
        }
}
