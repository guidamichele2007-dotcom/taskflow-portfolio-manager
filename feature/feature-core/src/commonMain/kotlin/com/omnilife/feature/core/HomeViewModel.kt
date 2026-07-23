package com.omnilife.feature.core

import com.omnilife.core.notifications.NotificationBroker
import com.omnilife.core.notifications.NotificationHistoryStore
import com.omnilife.core.notifications.NotificationOutcome
import com.omnilife.core.notifications.NotificationState
import com.omnilife.core.search.InMemoryRecentSearchStore
import com.omnilife.core.search.RecentSearchStore
import com.omnilife.core.search.UnifiedSearchService
import com.omnilife.core.sync.SyncStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * MVI store for the Home "Oggi" screen (HOME-001…008, TDR-02). Pure Kotlin — no Compose
 * dependency, fully unit-testable on the JVM target (README-BUILD.md §11), same pattern as
 * `feature-task`'s `TaskListViewModel`.
 *
 * Composes exactly the four Core services this sprint scopes: [syncStateManager] (`core-sync`),
 * [notificationBroker]/[notificationHistoryStore] (`core-notifications`), [searchService]
 * (`core-search`), and [widgetRegistry] (`core-designsystem`-rendered, module-local). No
 * `domain-*` dependency — every widget's content is a functional placeholder (see
 * [placeholderSection]), ready to be replaced with real domain data in a future sprint without
 * changing this class's public shape.
 */
public class HomeViewModel(
    private val syncStateManager: SyncStateManager,
    private val notificationBroker: NotificationBroker,
    private val notificationHistoryStore: NotificationHistoryStore,
    private val searchService: UnifiedSearchService,
    private val recentSearchStore: RecentSearchStore = InMemoryRecentSearchStore(),
    private val widgetRegistry: HomeWidgetRegistry = InMemoryHomeWidgetRegistry(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(HomeUiState())
    public val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(syncStatus = syncStateManager.current()) }
        // core-sync's SyncStateManager.observe has no unsubscribe mechanism (see
        // sprint4_report.md, "problemi trovati") — this listener is accepted to live as long as
        // syncStateManager does, same lifetime as this ViewModel in every real composition root.
        syncStateManager.observe { newState -> _state.update { it.copy(syncStatus = newState) } }
        refreshNotificationSummary()
        loadSections()
    }

    public fun dispatch(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.ReorderWidgets -> {
                widgetRegistry.reorder(intent.orderedKinds)
                _state.update { it.copy(widgetOrder = widgetRegistry.activeWidgets()) }
            }

            is HomeIntent.SetWidgetActive -> {
                widgetRegistry.setActive(intent.kind, intent.active)
                _state.update { it.copy(widgetOrder = widgetRegistry.activeWidgets()) }
            }

            is HomeIntent.Search -> handleSearch(intent.query)

            is HomeIntent.PerformQuickAction -> {
                // Placeholder: no domain-* module is wired this sprint, so a quick action has
                // nothing real to invoke yet. Recorded on state so a future sprint's domain
                // wiring — and this sprint's tests — can observe that the intent was received.
                _state.update { it.copy(lastQuickActionId = intent.actionId) }
            }

            HomeIntent.ToggleNotificationCenter ->
                _state.update { it.copy(notificationCenterOpen = !it.notificationCenterOpen) }

            is HomeIntent.DismissNotification -> {
                val request = notificationHistoryStore.findById(intent.requestId)
                if (request != null) {
                    notificationBroker.recordOutcome(request, NotificationOutcome.IGNORATA)
                    refreshNotificationSummary()
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        if (query.isBlank()) {
            _state.update {
                it.copy(searchQuery = query, searchResults = emptyList(), recentSearches = recentSearchStore.recent())
            }
            return
        }
        val results = searchService.search(query)
        _state.update { it.copy(searchQuery = query, searchResults = results) }
    }

    private fun refreshNotificationSummary() {
        val recent = notificationHistoryStore.recent()
        val pendingCount = recent.count { it.state == NotificationState.MOSTRATA }
        _state.update { it.copy(notificationSummary = HomeNotificationSummary(pendingCount, recent)) }
    }

    /**
     * HOME-001's <400ms cold-composition budget (see sprint4_report.md benchmark) — every
     * section resolves synchronously.
     */
    private fun loadSections() {
        val sections = HomeWidgetKind.entries.associateWith(::placeholderSection)
        _state.update { it.copy(widgetOrder = widgetRegistry.activeWidgets(), sections = sections) }
    }

    /**
     * Every widget's content this sprint: a domain-appropriate empty placeholder, never the
     * generic "0 moduli attivi" onboarding empty state (HOME §7) — that's for when a module is
     * genuinely deactivated, not for "not implemented yet."
     */
    private fun placeholderSection(kind: HomeWidgetKind): HomeSectionState<List<HomeListEntry>> =
        HomeSectionState.Empty(placeholderMessageFor(kind))

    private fun placeholderMessageFor(kind: HomeWidgetKind): String =
        when (kind) {
            HomeWidgetKind.TODAY_OVERVIEW -> "Il quadro del giorno arriva con il collegamento ai moduli attivi"
            HomeWidgetKind.AGENDA -> "L'agenda arriva con il modulo Calendario"
            HomeWidgetKind.GOAL_SUMMARY -> "Il riepilogo obiettivi arriva con il modulo Obiettivi"
            HomeWidgetKind.HABIT_SUMMARY -> "Il riepilogo abitudini arriva con il modulo Abitudini"
            HomeWidgetKind.FINANCE_SUMMARY -> "Il riepilogo finanze arriva con il modulo Finanze completo"
            HomeWidgetKind.CALENDAR_SUMMARY -> "Il riepilogo calendario arriva con il modulo Calendario completo"
            HomeWidgetKind.RECENT_ACTIVITY -> "L'attività recente arriva con il collegamento ai moduli attivi"
        }

    /** Cancels all in-flight work; call when the screen owning this store is disposed. */
    public fun clear() {
        scope.coroutineContext[Job]?.cancel()
    }
}
