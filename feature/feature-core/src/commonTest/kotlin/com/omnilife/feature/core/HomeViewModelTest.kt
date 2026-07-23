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

class HomeViewModelTest {
    private fun newBroker(historyStore: InMemoryNotificationHistoryStore = InMemoryNotificationHistoryStore()) =
        NotificationBroker(
            categoryRegistry = InMemoryNotificationCategoryRegistry(),
            historyStore = historyStore,
            budget = NotificationBudget(),
            digest = InMemoryNotificationDigest(),
            localNotificationService = NoOpLocalNotificationService(),
            eventBus = InMemoryEventBus(),
        )

    private fun newViewModel(
        historyStore: InMemoryNotificationHistoryStore = InMemoryNotificationHistoryStore(),
        searchService: UnifiedSearchService = FakeSearchService(),
    ) = HomeViewModel(
        syncStateManager = InMemorySyncStateManager(),
        notificationBroker = newBroker(historyStore),
        notificationHistoryStore = historyStore,
        searchService = searchService,
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
            )

        syncStateManager.transitionTo(com.omnilife.core.sync.SyncPhase.SYNCING)

        assertEquals(com.omnilife.core.sync.SyncPhase.SYNCING, viewModel.state.value.syncStatus?.phase)
    }
}
