package com.omnilife.feature.task.bridge

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.notifications.InMemoryNotificationCategoryRegistry
import com.omnilife.core.notifications.InMemoryNotificationDigest
import com.omnilife.core.notifications.InMemoryNotificationHistoryStore
import com.omnilife.core.notifications.LocalNotificationService
import com.omnilife.core.notifications.NotificationBroker
import com.omnilife.core.notifications.NotificationBudget
import com.omnilife.core.notifications.NotificationChannelSpec
import com.omnilife.core.notifications.NotificationRequest
import com.omnilife.domain.task.ReminderConfig
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
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}

private class RecordingLocalNotificationService : LocalNotificationService {
    val scheduled = mutableListOf<NotificationRequest>()
    val cancelled = mutableListOf<String>()

    override fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    ) {
        scheduled += request
    }

    override fun cancel(requestId: String) {
        cancelled += requestId
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskNotificationBridgeTest {
    private val zone = TimeZone.UTC
    private val now = Instant.parse("2026-01-01T08:00:00Z")

    private fun newBridge(
        repository: FakeTaskRepository,
        eventBus: InMemoryEventBus,
        localService: RecordingLocalNotificationService,
        scope: CoroutineScope,
    ): TaskNotificationBridge {
        val broker =
            NotificationBroker(
                categoryRegistry = InMemoryNotificationCategoryRegistry(),
                historyStore = InMemoryNotificationHistoryStore(),
                budget = NotificationBudget(),
                digest = InMemoryNotificationDigest(),
                localNotificationService = localService,
                eventBus = eventBus,
            )
        return TaskNotificationBridge(
            repository = repository,
            notificationBroker = broker,
            eventBus = eventBus,
            scope = scope,
            clock = FixedClock(now),
            zone = zone,
        )
    }

    @Test
    fun `creating a task with a due time and a reminder schedules a notification`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))

            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(leadMinutesBeforeDue = 30),
                    ),
            )

            assertEquals(listOf("task-1"), localService.scheduled.map { it.id })
            assertEquals(Instant.parse("2026-01-02T08:30:00Z"), localService.scheduled.single().scheduledFor)
        }

    @Test
    fun `a task with no reminder config schedules nothing`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))

            createTask(
                "Buy milk",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details = NewTaskDetails(dueDate = LocalDate(2026, 1, 2), dueTime = LocalTime(9, 0)),
            )

            assertTrue(localService.scheduled.isEmpty())
        }

    @Test
    fun `a due date without a due time schedules nothing (ReminderConfig requires a time-of-day)`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))

            createTask(
                "Buy milk",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details = NewTaskDetails(dueDate = LocalDate(2026, 1, 2), reminderConfig = ReminderConfig()),
            )

            assertTrue(localService.scheduled.isEmpty())
        }

    @Test
    fun `completing a task cancels its scheduled reminder`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))
            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(),
                    ),
            )
            val completeTask = CompleteTask(repository, eventBus, newId = { "task-2" }, clock = FixedClock(now))

            completeTask("task-1")

            assertTrue("task-1" in localService.cancelled)
        }

    @Test
    fun `deleting a task cancels its scheduled reminder`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))
            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(),
                    ),
            )
            val deleteTask = DeleteTask(repository, eventBus, clock = FixedClock(now))

            deleteTask("task-1")

            assertTrue("task-1" in localService.cancelled)
        }

    @Test
    fun `restoring a trashed task with a reminder reschedules it (Sprint 6 fix)`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))
            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(),
                    ),
            )
            val deleteTask = DeleteTask(repository, eventBus, clock = FixedClock(now))
            deleteTask("task-1")
            assertTrue("task-1" in localService.cancelled)
            val restoreTask = RestoreTask(repository, eventBus, clock = FixedClock(now))

            restoreTask("task-1")

            assertEquals(listOf("task-1"), localService.scheduled.map { it.id })
        }

    @Test
    fun `editing the due time reschedules the reminder to the new time, not the old one`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))
            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(),
                    ),
            )
            val updateTaskFields = UpdateTaskFields(repository, eventBus, clock = FixedClock(now))

            updateTaskFields("task-1", TaskFieldEdits(dueTime = Edit.Set(LocalTime(15, 0))))

            assertEquals("task-1", localService.cancelled.last())
            assertEquals(Instant.parse("2026-01-02T15:00:00Z"), localService.scheduled.last().scheduledFor)
        }

    @Test
    fun `after clear, further task events no longer schedule or cancel anything`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            val bridge = newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))

            bridge.clear()
            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(),
                    ),
            )

            assertTrue(localService.scheduled.isEmpty())
        }

    @Test
    fun `reconcileAll (MVP Release 1_0 reboot-survival fix) re-schedules every active task's reminder`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            val bridge = newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))
            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(leadMinutesBeforeDue = 30),
                    ),
            )
            // Simulates AlarmManager having forgotten the alarm across a reboot: nothing to cancel
            // is asserted here, only that reconcileAll independently re-derives and re-schedules it.
            localService.scheduled.clear()

            bridge.reconcileAll()

            assertEquals(listOf("task-1"), localService.scheduled.map { it.id })
            assertEquals(Instant.parse("2026-01-02T08:30:00Z"), localService.scheduled.single().scheduledFor)
        }

    @Test
    fun `reconcileAll never re-schedules a completed task's reminder`() =
        runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val localService = RecordingLocalNotificationService()
            val scope = CoroutineScope(UnconfinedTestDispatcher())
            val bridge = newBridge(repository, eventBus, localService, scope)
            val createTask = CreateTask(repository, eventBus, newId = { "task-1" }, clock = FixedClock(now))
            createTask(
                "Call the dentist",
                listId = "list-1",
                ownerAccountId = "acc-1",
                deviceId = "dev-1",
                details =
                    NewTaskDetails(
                        dueDate = LocalDate(2026, 1, 2),
                        dueTime = LocalTime(9, 0),
                        reminderConfig = ReminderConfig(),
                    ),
            )
            val completeTask = CompleteTask(repository, eventBus, newId = { "task-2" }, clock = FixedClock(now))
            completeTask("task-1")
            localService.scheduled.clear()

            bridge.reconcileAll()

            assertTrue(localService.scheduled.isEmpty())
        }
}
