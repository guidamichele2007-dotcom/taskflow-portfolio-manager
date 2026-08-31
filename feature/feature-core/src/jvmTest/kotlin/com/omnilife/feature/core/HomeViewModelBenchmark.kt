package com.omnilife.feature.core

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.core.notifications.InMemoryNotificationCategoryRegistry
import com.omnilife.core.notifications.InMemoryNotificationDigest
import com.omnilife.core.notifications.InMemoryNotificationHistoryStore
import com.omnilife.core.notifications.LocalNotificationService
import com.omnilife.core.notifications.NotificationBroker
import com.omnilife.core.notifications.NotificationBudget
import com.omnilife.core.notifications.NotificationChannelSpec
import com.omnilife.core.notifications.NotificationRequest
import com.omnilife.core.search.SearchFilter
import com.omnilife.core.search.SearchResult
import com.omnilife.core.search.UnifiedSearchService
import com.omnilife.core.sync.InMemorySyncStateManager
import com.omnilife.feature.core.onboarding.FakeTaskRepository
import kotlin.system.measureNanoTime
import kotlin.test.Test
import kotlin.test.assertTrue

private class BenchmarkNoOpLocalNotificationService : LocalNotificationService {
    override fun show(
        request: NotificationRequest,
        channel: NotificationChannelSpec,
        onDelivered: (NotificationRequest) -> Unit,
    ) = Unit

    override fun cancel(requestId: String) = Unit
}

private class EmptySearchService : UnifiedSearchService {
    override fun search(
        query: String,
        filter: SearchFilter,
    ): List<SearchResult> = emptyList()
}

/**
 * HOME-001's explicit budget: "compone in < 400 ms a freddo con 5 moduli attivi." All 7 widget
 * kinds are active here (a stricter test than the Bible's literal "5"). Hand-rolled benchmark
 * (see sprint4_report.md for why not JMH), same pattern as every other Core module's benchmarks
 * this sprint sequence.
 */
class HomeViewModelBenchmark {
    @Test
    fun `benchmark - HOME-001 cold composition stays under 400ms with every widget active`() {
        val elapsedNanos =
            measureNanoTime {
                HomeViewModel(
                    syncStateManager = InMemorySyncStateManager(),
                    notificationBroker =
                        NotificationBroker(
                            categoryRegistry = InMemoryNotificationCategoryRegistry(),
                            historyStore = InMemoryNotificationHistoryStore(),
                            budget = NotificationBudget(),
                            digest = InMemoryNotificationDigest(),
                            localNotificationService = BenchmarkNoOpLocalNotificationService(),
                            eventBus = InMemoryEventBus(),
                        ),
                    notificationHistoryStore = InMemoryNotificationHistoryStore(),
                    searchService = EmptySearchService(),
                    taskRepository = FakeTaskRepository(),
                    eventBus = InMemoryEventBus(),
                )
            }

        val elapsedMs = elapsedNanos / 1_000_000
        println(
            "[benchmark] HomeViewModel cold composition (7 active widgets): ${elapsedMs}ms (HOME-001 target: <400ms)",
        )
        assertTrue(elapsedMs < 400, "HOME-001 requires cold composition under 400ms, was ${elapsedMs}ms")
    }
}
