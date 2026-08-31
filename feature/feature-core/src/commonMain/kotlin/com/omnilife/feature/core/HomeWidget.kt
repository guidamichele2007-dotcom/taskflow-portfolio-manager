package com.omnilife.feature.core

/**
 * Every Home widget kind requested this sprint (HOME-002: "la Home include automaticamente le
 * card dei soli moduli attivi"). Default order follows HOME-R-03's spirit — dal vincolato al
 * volitivo — adapted since no domain module is wired this sprint (Today Overview stands in for
 * "eventi", Agenda for the day's schedule, then the summary widgets, Recent Activity last as
 * the most exploratory/least time-bound section).
 */
public enum class HomeWidgetKind {
    TODAY_OVERVIEW,
    AGENDA,
    GOAL_SUMMARY,
    HABIT_SUMMARY,
    FINANCE_SUMMARY,
    CALENDAR_SUMMARY,
    RECENT_ACTIVITY,
}

/**
 * HOME-002/HOME-003: which widgets are active right now, and in what order the user has chosen
 * (long-press reorder). No `domain-*`/`core-moduleregistry` dependency in this sprint (explicit
 * "usa esclusivamente" Core-only constraint) — every widget's *content* is a functional
 * placeholder (see [HomeViewModel]), but activation/reordering here are real and ready for a
 * future sprint to drive from real per-module activation without changing this contract.
 */
public interface HomeWidgetRegistry {
    public fun activeWidgets(): List<HomeWidgetKind>

    public fun setActive(
        kind: HomeWidgetKind,
        active: Boolean,
    )

    /** [orderedKinds] must be a permutation of the full widget set (active + inactive). */
    public fun reorder(orderedKinds: List<HomeWidgetKind>)
}

public class InMemoryHomeWidgetRegistry(
    defaultOrder: List<HomeWidgetKind> = DEFAULT_ORDER,
) : HomeWidgetRegistry {
    private var order = defaultOrder.toMutableList()
    private val inactive = mutableSetOf<HomeWidgetKind>()

    override fun activeWidgets(): List<HomeWidgetKind> = order.filterNot { it in inactive }

    override fun setActive(
        kind: HomeWidgetKind,
        active: Boolean,
    ) {
        if (active) inactive.remove(kind) else inactive.add(kind)
    }

    override fun reorder(orderedKinds: List<HomeWidgetKind>) {
        require(orderedKinds.toSet() == order.toSet()) {
            "reorder must be a permutation of the existing widget kinds, was $orderedKinds"
        }
        order = orderedKinds.toMutableList()
    }

    public companion object {
        public val DEFAULT_ORDER: List<HomeWidgetKind> =
            listOf(
                HomeWidgetKind.TODAY_OVERVIEW,
                HomeWidgetKind.AGENDA,
                HomeWidgetKind.GOAL_SUMMARY,
                HomeWidgetKind.HABIT_SUMMARY,
                HomeWidgetKind.FINANCE_SUMMARY,
                HomeWidgetKind.CALENDAR_SUMMARY,
                HomeWidgetKind.RECENT_ACTIVITY,
            )
    }
}
