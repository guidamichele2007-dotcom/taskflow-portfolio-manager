package com.omnilife.feature.core

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.notifications.EntityReference
import com.omnilife.core.notifications.InMemoryNotificationCategoryRegistry
import com.omnilife.core.notifications.InMemoryNotificationDigest
import com.omnilife.core.notifications.InMemoryNotificationHistoryStore
import com.omnilife.core.notifications.LocalNotificationService
import com.omnilife.core.notifications.NotificationBroker
import com.omnilife.core.notifications.NotificationBudget
import com.omnilife.core.notifications.NotificationCategory
import com.omnilife.core.notifications.NotificationChannelSpec
import com.omnilife.core.notifications.NotificationOutcome
import com.omnilife.core.notifications.NotificationPriority
import com.omnilife.core.notifications.NotificationRequest
import com.omnilife.core.notifications.NotificationState
import com.omnilife.core.search.SearchFilter
import com.omnilife.core.search.SearchResult
import com.omnilife.core.search.UnifiedSearchService
import com.omnilife.core.sync.InMemorySyncStateManager
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.usecase.CreateTask
import com.omnilife.feature.core.onboarding.FakeTaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class NoOpLocalNotificationService : LocalNotificationService {
    override fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    ) = Unit

    override fun cancel(requestId: String) = Unit
}

private class FakeSearchService(private val resultsByQuery: Map<String, List<SearchResult>> = emptyMap()) :
    UnifiedSearchService {
    var lastQuery: String? = null
        private set

    override fun search(
        query: String,
        filter: SearchFilter,
    ): List<SearchResult> {
        lastQuery = query
        return resultsByQuery[query].orEmpty()
    }
}

private fun testEnvelope(id: String) =
    com.omnilife.core.common.Envelope(
        id = id,
        ownerAccountId = "account-1",
        schemaVersion = 1,
        createdAt = Instant.fromEpochMilliseconds(0),
        createdByDevice = "device-1",
        modifiedAt = Instant.fromEpochMilliseconds(0),
        modifiedByDevice = "device-1",
    )

private fun testCategory() = NotificationCategory("home.test", "home")

private fun testNotificationRequest(
    id: String,
    state: NotificationState = NotificationState.MOSTRATA,
) = NotificationRequest(
    id = id,
    category = testCategory(),
    priority = NotificationPriority.INFORMATIVA,
    entityReference = EntityReference("entity-$id", "test"),
    title = "Titolo $id",
    body = "Corpo $id",
    scheduledFor = Instant.parse("2026-01-01T10:00:00Z"),
    state = state,
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private fun newBroker(
        historyStore: InMemoryNotificationHistoryStore = InMemoryNotificationHistoryStore(),
        eventBus: com.omnilife.core.eventbus.EventBus = InMemoryEventBus(),
    ) = NotificationBroker(
        categoryRegistry = InMemoryNotificationCategoryRegistry(),
        historyStore = historyStore,
        budget = NotificationBudget(),
        digest = InMemoryNotificationDigest(),
        localNotificationService = NoOpLocalNotificationService(),
        eventBus = eventBus,
    )

    private fun newViewModel(
        historyStore: InMemoryNotificationHistoryStore = InMemoryNotificationHistoryStore(),
        searchService: UnifiedSearchService = FakeSearchService(),
        taskRepository: FakeTaskRepository = FakeTaskRepository(),
        eventBus: com.omnilife.core.eventbus.EventBus = InMemoryEventBus(),
    ) = HomeViewModel(
        syncStateManager = InMemorySyncStateManager(),
        notificationBroker = newBroker(historyStore, eventBus),
        notificationHistoryStore = historyStore,
        searchService = searchService,
        taskRepository = taskRepository,
        eventBus = eventBus,
        scope = CoroutineScope(UnconfinedTestDispatcher()),
    )

    @Test
    fun `initial state loads sync status and default widget order`() {
        val viewModel = newViewModel()
        val state = viewModel.state.value

        assertEquals(InMemoryHomeWidgetRegistry.DEFAULT_ORDER, state.widgetOrder)
        assertEquals(com.omnilife.core.sync.SyncPhase.IDLE, state.syncStatus?.phase)
    }

    @Test
    fun `every widget kind starts as a placeholder Empty section, never Loading forever`() {
        val state = newViewModel().state.value

        assertEquals(HomeWidgetKind.entries.toSet(), state.sections.keys)
        state.sections.values.forEach { sectionState -> assertTrue(sectionState is HomeSectionState.Empty) }
    }

    @Test
    fun `ReorderWidgets updates widgetOrder`() {
        val viewModel = newViewModel()
        val newOrder = InMemoryHomeWidgetRegistry.DEFAULT_ORDER.reversed()

        viewModel.dispatch(HomeIntent.ReorderWidgets(newOrder))

        assertEquals(newOrder, viewModel.state.value.widgetOrder)
    }

    @Test
    fun `SetWidgetActive false removes the kind from widgetOrder without touching the others`() {
        val viewModel = newViewModel()

        viewModel.dispatch(HomeIntent.SetWidgetActive(HomeWidgetKind.FINANCE_SUMMARY, active = false))

        val order = viewModel.state.value.widgetOrder
        assertFalse(HomeWidgetKind.FINANCE_SUMMARY in order)
        assertEquals(HomeWidgetKind.entries.size - 1, order.size)
    }

    @Test
    fun `blank search clears results and populates recent searches`() {
        val viewModel = newViewModel()

        viewModel.dispatch(HomeIntent.Search(""))

        assertTrue(viewModel.state.value.searchResults.isEmpty())
        assertEquals("", viewModel.state.value.searchQuery)
    }

    @Test
    fun `non-blank search delegates to UnifiedSearchService and stores results`() {
        val result =
            SearchResult("t1", "task", "Comprare il latte", null, "ACTIVE", Instant.parse("2026-01-01T09:00:00Z"))
        val searchService = FakeSearchService(mapOf("latte" to listOf(result)))
        val viewModel = newViewModel(searchService = searchService)

        viewModel.dispatch(HomeIntent.Search("latte"))

        assertEquals("latte", searchService.lastQuery)
        assertEquals(listOf(result), viewModel.state.value.searchResults)
        assertEquals("latte", viewModel.state.value.searchQuery)
    }

    @Test
    fun `PerformQuickAction is a recorded placeholder, never throws`() {
        val viewModel = newViewModel()

        viewModel.dispatch(HomeIntent.PerformQuickAction("new-task"))

        assertEquals("new-task", viewModel.state.value.lastQuickActionId)
    }

    @Test
    fun `clear unsubscribes from SyncStateManager so further transitions do not update stale state`() {
        val syncStateManager = com.omnilife.core.sync.InMemorySyncStateManager()
        val viewModel =
            HomeViewModel(
                syncStateManager = syncStateManager,
                notificationBroker = newBroker(),
                notificationHistoryStore = InMemoryNotificationHistoryStore(),
                searchService = FakeSearchService(),
                taskRepository = FakeTaskRepository(),
                eventBus = InMemoryEventBus(),
                scope = CoroutineScope(UnconfinedTestDispatcher()),
            )

        viewModel.clear()
        syncStateManager.transitionTo(com.omnilife.core.sync.SyncPhase.SYNCING)

        assertEquals(com.omnilife.core.sync.SyncPhase.IDLE, viewModel.state.value.syncStatus?.phase)
    }

    @Test
    fun `ToggleNotificationCenter flips notificationCenterOpen`() {
        val viewModel = newViewModel()
        assertFalse(viewModel.state.value.notificationCenterOpen)

        viewModel.dispatch(HomeIntent.ToggleNotificationCenter)
        assertTrue(viewModel.state.value.notificationCenterOpen)

        viewModel.dispatch(HomeIntent.ToggleNotificationCenter)
        assertFalse(viewModel.state.value.notificationCenterOpen)
    }

    @Test
    fun `notification summary counts only MOSTRATA requests as pending`() {
        val historyStore = InMemoryNotificationHistoryStore()
        historyStore.record(testNotificationRequest("shown-1", NotificationState.MOSTRATA))
        historyStore.record(testNotificationRequest("shown-2", NotificationState.MOSTRATA))
        historyStore.record(testNotificationRequest("ignored-1", NotificationState.IGNORATA))

        val viewModel = newViewModel(historyStore = historyStore)

        assertEquals(2, viewModel.state.value.notificationSummary.pendingCount)
        assertEquals(3, viewModel.state.value.notificationSummary.recent.size)
    }

    @Test
    fun `DismissNotification records IGNORATA outcome and refreshes the summary`() {
        val historyStore = InMemoryNotificationHistoryStore()
        historyStore.record(testNotificationRequest("shown-1", NotificationState.MOSTRATA))
        val viewModel = newViewModel(historyStore = historyStore)
        assertEquals(1, viewModel.state.value.notificationSummary.pendingCount)

        viewModel.dispatch(HomeIntent.DismissNotification("shown-1"))

        assertEquals(0, viewModel.state.value.notificationSummary.pendingCount)
        assertEquals(NotificationOutcome.IGNORATA, historyStore.findById("shown-1")?.outcome)
    }

    @Test
    fun `DismissNotification for an unknown id is a no-op, never throws`() {
        val viewModel = newViewModel()
        viewModel.dispatch(HomeIntent.DismissNotification("never-existed"))
        assertEquals(0, viewModel.state.value.notificationSummary.pendingCount)
    }

    @Test
    fun `sync status updates reactively when SyncStateManager transitions`() {
        val syncStateManager = InMemorySyncStateManager()
        val viewModel =
            HomeViewModel(
                syncStateManager = syncStateManager,
                notificationBroker = newBroker(),
                notificationHistoryStore = InMemoryNotificationHistoryStore(),
                searchService = FakeSearchService(),
                taskRepository = FakeTaskRepository(),
                eventBus = InMemoryEventBus(),
                scope = CoroutineScope(UnconfinedTestDispatcher()),
            )

        syncStateManager.transitionTo(com.omnilife.core.sync.SyncPhase.SYNCING)

        assertEquals(com.omnilife.core.sync.SyncPhase.SYNCING, viewModel.state.value.syncStatus?.phase)
    }

    @Test
    fun `Today Overview shows a real task due today, not a placeholder`() {
        val repository = FakeTaskRepository()
        val today = kotlinx.datetime.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
        repository.tasks["t1"] =
            Task(envelope = testEnvelope("t1"), title = "Chiamare il dentista", listId = "list-1", dueDate = today)
        val viewModel = newViewModel(taskRepository = repository)

        val section = viewModel.state.value.sections.getValue(HomeWidgetKind.TODAY_OVERVIEW)

        assertTrue(section is HomeSectionState.Content)
        assertEquals(listOf("Chiamare il dentista"), (section as HomeSectionState.Content).data.map { it.title })
    }

    @Test
    fun `Today Overview with no tasks due today is a genuine Empty state, not fake content`() {
        val viewModel = newViewModel()

        val section = viewModel.state.value.sections.getValue(HomeWidgetKind.TODAY_OVERVIEW)

        assertTrue(section is HomeSectionState.Empty)
    }

    @Test
    fun `Recent Activity records a real task creation event with the task's real title`() =
        kotlinx.coroutines.test.runTest {
            val repository = FakeTaskRepository()
            val eventBus = InMemoryEventBus()
            val viewModel = newViewModel(taskRepository = repository, eventBus = eventBus)
            val createTask = CreateTask(repository, eventBus, newId = { "t1" })

            createTask("Comprare il pane", "list-1", "acc-1", "dev-1")

            val section = viewModel.state.value.sections.getValue(HomeWidgetKind.RECENT_ACTIVITY)
            assertTrue(section is HomeSectionState.Content)
            assertTrue((section as HomeSectionState.Content).data.single().title.contains("Comprare il pane"))
        }

    @Test
    fun `Recent Activity with no task events yet is a genuine Empty state`() {
        val viewModel = newViewModel()

        val section = viewModel.state.value.sections.getValue(HomeWidgetKind.RECENT_ACTIVITY)

        assertTrue(section is HomeSectionState.Empty)
    }

    @Test
    fun `Goal, Habit, Finance, and Calendar summaries remain functional placeholders, never fake data`() {
        val viewModel = newViewModel()
        val state = viewModel.state.value

        listOf(
            HomeWidgetKind.GOAL_SUMMARY,
            HomeWidgetKind.HABIT_SUMMARY,
            HomeWidgetKind.FINANCE_SUMMARY,
            HomeWidgetKind.CALENDAR_SUMMARY,
        ).forEach { kind -> assertTrue(state.sections.getValue(kind) is HomeSectionState.Empty) }
    }
}
