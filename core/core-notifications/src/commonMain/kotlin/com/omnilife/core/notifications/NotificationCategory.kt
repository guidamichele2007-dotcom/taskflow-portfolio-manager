package com.omnilife.core.notifications

/**
 * NTF-007/NTF-R-03: "ogni notifica è disattivabile alla granularità a cui è generata, dal suo
 * stesso menu" — a category is per-module/per-type (e.g. `task.reminder`, `habit.streak-risk`),
 * never a single global on/off switch. [moduleName] is the owning module's identifier only
 * (this module never imports a domain-* type), [defaultEnabled] is the out-of-the-box state.
 */
public data class NotificationCategory(
    public val id: String,
    public val moduleName: String,
    public val defaultEnabled: Boolean = true,
)

/**
 * NTF-006: "una categoria ignorata 3 volte consecutive propone la propria disattivazione."
 * Tracks per-category enablement and the consecutive-ignore streak that feeds that proposal —
 * two concerns that share the same key ([NotificationCategory.id]) but are otherwise
 * independent (a disabled category has no further streak to track).
 */
public interface NotificationCategoryRegistry {
    public fun register(category: NotificationCategory)

    public fun isEnabled(categoryId: String): Boolean

    public fun setEnabled(
        categoryId: String,
        enabled: Boolean,
    )

    /** Resets to 0 on any non-ignored outcome; call after every resolved [NotificationRequest]. */
    public fun recordOutcome(
        categoryId: String,
        outcome: NotificationOutcome,
    )

    /** NTF-006/NTF-AC-04: true once [recordOutcome] has seen 3 consecutive [NotificationOutcome.IGNORATA]. */
    public fun shouldProposeAutoDisable(categoryId: String): Boolean

    public fun categories(): List<NotificationCategory>
}

public class InMemoryNotificationCategoryRegistry : NotificationCategoryRegistry {
    private companion object {
        const val AUTO_DISABLE_THRESHOLD = 3
    }

    private val categories = mutableMapOf<String, NotificationCategory>()
    private val enabled = mutableMapOf<String, Boolean>()
    private val consecutiveIgnored = mutableMapOf<String, Int>()

    override fun register(category: NotificationCategory) {
        categories[category.id] = category
        enabled.putIfAbsent(category.id, category.defaultEnabled)
    }

    override fun isEnabled(categoryId: String): Boolean = enabled[categoryId] ?: true

    override fun setEnabled(
        categoryId: String,
        enabled: Boolean,
    ) {
        this.enabled[categoryId] = enabled
    }

    override fun recordOutcome(
        categoryId: String,
        outcome: NotificationOutcome,
    ) {
        consecutiveIgnored[categoryId] =
            if (outcome == NotificationOutcome.IGNORATA) {
                (consecutiveIgnored[categoryId] ?: 0) + 1
            } else {
                0
            }
    }

    override fun shouldProposeAutoDisable(categoryId: String): Boolean =
        (consecutiveIgnored[categoryId] ?: 0) >= AUTO_DISABLE_THRESHOLD

    override fun categories(): List<NotificationCategory> = categories.values.toList()
}
