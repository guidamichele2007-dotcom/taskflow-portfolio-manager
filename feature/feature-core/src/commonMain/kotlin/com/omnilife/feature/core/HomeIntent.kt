package com.omnilife.feature.core

/**
 * User intents for the Home "Oggi" screen (MVI/UDF, TDR-02). No `Refresh` intent exists:
 * HOME-007 forbids pull-to-refresh by design.
 */
public sealed interface HomeIntent {
    /** HOME-003: long-press reorder. */
    public data class ReorderWidgets(public val orderedKinds: List<HomeWidgetKind>) : HomeIntent

    /** HOME-002: a module (widget) turning on/off. */
    public data class SetWidgetActive(public val kind: HomeWidgetKind, public val active: Boolean) : HomeIntent

    /** Global Search Entry — incremental, no submit button (SRCH-001). */
    public data class Search(public val query: String) : HomeIntent

    /** HOME-004-adjacent. Execution is a placeholder this sprint — see HomeViewModel. */
    public data class PerformQuickAction(public val actionId: String) : HomeIntent

    public data object ToggleNotificationCenter : HomeIntent

    public data class DismissNotification(public val requestId: String) : HomeIntent
}
