package com.omnilife.feature.core

/**
 * The Loading/Empty/Error/Content states every Home content section needs (Today Overview,
 * Agenda, Recent Activity, Goal/Habit/Finance/Calendar summaries) — one shared shape instead of
 * ad hoc per-section booleans, so every section renders identically (Design System Bible: one
 * state per meaning, never a bespoke variant per screen — the same principle `OmniEmptyState`/
 * `OmniLoadingState`/`OmniErrorState` already enforce at the component level).
 */
public sealed interface HomeSectionState<out T> {
    public data object Loading : HomeSectionState<Nothing>

    public data class Error(public val message: String) : HomeSectionState<Nothing>

    public data class Empty(public val message: String) : HomeSectionState<Nothing>

    public data class Content<T>(public val data: T) : HomeSectionState<T>
}
