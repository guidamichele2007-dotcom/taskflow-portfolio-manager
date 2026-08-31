package com.omnilife.feature.core

import com.omnilife.core.common.EntityId
import com.omnilife.core.eventbus.EventBus
import com.omnilife.core.eventbus.Subscription
import com.omnilife.core.eventbus.subscribe
import com.omnilife.core.notifications.NotificationBroker
import com.omnilife.core.notifications.NotificationHistoryStore
import com.omnilife.core.notifications.NotificationOutcome
import com.omnilife.core.notifications.NotificationState
import com.omnilife.core.search.InMemoryRecentSearchStore
import com.omnilife.core.search.RecentSearchStore
import com.omnilife.core.search.UnifiedSearchService
import com.omnilife.core.sync.SyncStateManager
import com.omnilife.core.sync.SyncStateSubscription
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.TaskEvent
import com.omnilife.domain.task.TaskRepository
import com.omnilife.domain.task.usecase.GetTasksForView
import com.omnilife.domain.task.usecase.TaskListMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI store for the Home "Oggi" screen (HOME-001…008, TDR-02). Pure Kotlin — no Compose
 * dependency, fully unit-testable on the JVM target (README-BUILD.md §11), same pattern as
 * `feature-task`'s `TaskListViewModel`.
 *
 * Macro Sprint 5: Today Overview, Agenda, and Recent Activity now show real `domain-task` data —
 * [taskRepository]/[getTasksForView] for the first two, a bounded in-memory log of [TaskEvent]
 * (subscribed via [eventBus]) for the third, since the Event Bus is explicitly "not an
 * event-store" (core-eventbus's own doc) and keeps no history of its own. Goal/Habit/Finance/
 * Calendar summaries stay functional placeholders — those domain modules are not implemented this
 * sprint, and this widget set never invents data for them.
 */
public class HomeViewModel(
    private val syncStateManager: SyncStateManager,
    private val notificationBroker: NotificationBroker,
    private val notificationHistoryStore: NotificationHistoryStore,
    private val searchService: UnifiedSearchService,
    private val taskRepository: TaskRepository,
    eventBus: EventBus,
    private val recentSearchStore: RecentSearchStore = InMemoryRecentSearchStore(),
    private val widgetRegistry: HomeWidgetRegistry = InMemoryHomeWidgetRegistry(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    // Not a constructor parameter (detekt LongParameterList, threshold 10): no caller needs a
    // custom GetTasksForView — it's always the same use case built directly over taskRepository.
    private val getTasksForView: GetTasksForView = GetTasksForView(taskRepository)

    private val _state = MutableStateFlow(HomeUiState())
    public val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val syncStateSubscription: SyncStateSubscription
    private val recentActivity = ArrayDeque<HomeListEntry>()
    private val taskEventSubscriptions: List<Subscription>

    init {
        _state.update { it.copy(syncStatus = syncStateManager.current()) }
        // TDR-34: SyncStateManager.observe now returns a Subscription — held here and cancelled
        // in clear() so this ViewModel's listener does not outlive it (fixes the leak documented
        // in sprint4_report.md, "problemi trovati").
        syncStateSubscription =
            syncStateManager.observe { newState -> _state.update { it.copy(syncStatus = newState) } }
        taskEventSubscriptions =
            listOf(
                eventBus.subscribe<TaskEvent.Created> { recordActivity(it.taskId, "creato") },
                eventBus.subscribe<TaskEvent.Completed> { recordActivity(it.taskId, "completato") },
                eventBus.subscribe<TaskEvent.Uncompleted> { recordActivity(it.taskId, "riaperto") },
                eventBus.subscribe<TaskEvent.Updated> { recordActivity(it.taskId, "modificato") },
                eventBus.subscribe<TaskEvent.Rescheduled> { recordActivity(it.taskId, "riprogrammato") },
                eventBus.subscribe<TaskEvent.Deleted> { recordActivity(it.taskId, "eliminato") },
            )
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

            is HomeIntent.PerformQuickAction ->
                _state.update { it.copy(lastQuickActionId = intent.actionId) }

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
     * HOME-001's <400ms cold-composition budget (see sprint4_report.md benchmark) — the
     * constructor itself only schedules this, never blocks on it; sections start Loading and
     * resolve asynchronously, same pattern as `TaskListViewModel.refresh`.
     */
    private fun loadSections() {
        val placeholders =
            PLACEHOLDER_WIDGET_KINDS.associateWith { kind -> HomeSectionState.Empty(placeholderMessageFor(kind)) }
        _state.update {
            it.copy(
                widgetOrder = widgetRegistry.activeWidgets(),
                sections =
                    placeholders +
                        mapOf(
                            HomeWidgetKind.TODAY_OVERVIEW to HomeSectionState.Loading,
                            HomeWidgetKind.AGENDA to HomeSectionState.Loading,
                            HomeWidgetKind.RECENT_ACTIVITY to currentRecentActivitySection(),
                        ),
            )
        }
        scope.launch {
            val today = getTasksForView(TaskListMode.TODAY).take(MAX_WIDGET_ENTRIES).map { it.toHomeListEntry() }
            _state.update { it.copy(sections = it.sections + (HomeWidgetKind.TODAY_OVERVIEW to today.toSection())) }
        }
        scope.launch {
            // Agenda widget scope: task due dates only (no `domain-calendar`, not implemented this
            // sprint) — genuinely available data, not a stand-in for a real calendar surface.
            val upcoming = getTasksForView(TaskListMode.UPCOMING).take(MAX_WIDGET_ENTRIES).map { it.toHomeListEntry() }
            _state.update { it.copy(sections = it.sections + (HomeWidgetKind.AGENDA to upcoming.toSection())) }
        }
    }

    private fun List<HomeListEntry>.toSection(): HomeSectionState<List<HomeListEntry>> =
        if (isEmpty()) HomeSectionState.Empty("Niente in programma") else HomeSectionState.Content(this)

    private fun recordActivity(
        taskId: EntityId,
        actionLabel: String,
    ) {
        scope.launch {
            val task = taskRepository.findTaskById(taskId)
            val title = task?.title ?: taskId
            val entry = HomeListEntry(id = "$taskId-${recentActivity.size}", title = "$title — $actionLabel")
            recentActivity.addFirst(entry)
            while (recentActivity.size > MAX_RECENT_ACTIVITY) recentActivity.removeLast()
            _state.update {
                it.copy(sections = it.sections + (HomeWidgetKind.RECENT_ACTIVITY to currentRecentActivitySection()))
            }
        }
    }

    private fun currentRecentActivitySection(): HomeSectionState<List<HomeListEntry>> =
        recentActivity.toList().toSection().let { section ->
            if (section is HomeSectionState.Empty) {
                HomeSectionState.Empty("Nessuna attività recente")
            } else {
                section
            }
        }

    private fun placeholderMessageFor(kind: HomeWidgetKind): String =
        when (kind) {
            HomeWidgetKind.GOAL_SUMMARY -> "Il riepilogo obiettivi arriva con il modulo Obiettivi"
            HomeWidgetKind.HABIT_SUMMARY -> "Il riepilogo abitudini arriva con il modulo Abitudini"
            HomeWidgetKind.FINANCE_SUMMARY -> "Il riepilogo finanze arriva con il modulo Finanze completo"
            HomeWidgetKind.CALENDAR_SUMMARY -> "Il riepilogo calendario arriva con il modulo Calendario completo"
            else -> error("$kind has real data, not a placeholder")
        }

    /** Cancels all in-flight work, the sync-state listener, and every task-event subscription. */
    public fun clear() {
        syncStateSubscription.cancel()
        taskEventSubscriptions.forEach { it.cancel() }
        scope.coroutineContext[Job]?.cancel()
    }

    private companion object {
        const val MAX_WIDGET_ENTRIES = 5
        const val MAX_RECENT_ACTIVITY = 20
        val PLACEHOLDER_WIDGET_KINDS =
            listOf(
                HomeWidgetKind.GOAL_SUMMARY,
                HomeWidgetKind.HABIT_SUMMARY,
                HomeWidgetKind.FINANCE_SUMMARY,
                HomeWidgetKind.CALENDAR_SUMMARY,
            )
    }
}

private fun Task.toHomeListEntry(): HomeListEntry =
    HomeListEntry(
        id = envelope.id,
        title = title,
        secondaryText = dueDate?.let { date -> if (dueTime != null) "$date $dueTime" else date.toString() },
        completed = completed,
    )
