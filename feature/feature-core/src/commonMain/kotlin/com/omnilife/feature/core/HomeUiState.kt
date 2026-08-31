package com.omnilife.feature.core

import com.omnilife.core.notifications.NotificationRequest
import com.omnilife.core.search.SearchResult
import com.omnilife.core.sync.SyncState

/**
 * A single row inside a widget's content list — generic across every domain, mirrors
 * OmniListItem's shared anatomy.
 */
public data class HomeListEntry(
    public val id: String,
    public val title: String,
    public val secondaryText: String? = null,
    public val completed: Boolean? = null,
)

/** Quick Actions: a generic descriptor. Handlers are placeholders this sprint — see HomeViewModel. */
public data class HomeQuickAction(
    public val id: String,
    public val label: String,
)

/**
 * NTF-007-adjacent: what the Home's notification-center entry shows — [pendingCount] is
 * requests in [com.omnilife.core.notifications.NotificationState.MOSTRATA] (shown, not yet
 * resolved), sourced directly from `core-notifications`' `NotificationHistoryStore`.
 */
public data class HomeNotificationSummary(
    public val pendingCount: Int,
    public val recent: List<NotificationRequest>,
)

/**
 * MVI state for the Home "Oggi" screen (HOME-001…008). [sections] covers Today Overview,
 * Agenda, Recent Activity, Goal/Habit/Finance/Calendar summaries — all functional placeholders
 * in this sprint (no `domain-*` dependency is in scope: "usa esclusivamente" Core UI
 * Kit/Search/Sync/Notifications) — while [syncStatus], [notificationSummary], and the search
 * fields are wired to real Core services. Deliberately has no "isRefreshing"/pull-to-refresh
 * field: HOME-007 forbids the gesture by design (data is always locally reactive).
 */
public data class HomeUiState(
    public val widgetOrder: List<HomeWidgetKind> = InMemoryHomeWidgetRegistry.DEFAULT_ORDER,
    public val sections: Map<HomeWidgetKind, HomeSectionState<List<HomeListEntry>>> = emptyMap(),
    public val quickActions: List<HomeQuickAction> = DEFAULT_QUICK_ACTIONS,
    public val lastQuickActionId: String? = null,
    public val syncStatus: SyncState? = null,
    public val notificationSummary: HomeNotificationSummary = HomeNotificationSummary(0, emptyList()),
    public val notificationCenterOpen: Boolean = false,
    public val searchQuery: String = "",
    public val searchResults: List<SearchResult> = emptyList(),
    public val recentSearches: List<String> = emptyList(),
) {
    public companion object {
        public val DEFAULT_QUICK_ACTIONS: List<HomeQuickAction> =
            listOf(
                HomeQuickAction("new-task", "Nuova attività"),
                HomeQuickAction("new-note", "Nuova nota"),
                HomeQuickAction("new-transaction", "Nuova transazione"),
            )
    }
}
