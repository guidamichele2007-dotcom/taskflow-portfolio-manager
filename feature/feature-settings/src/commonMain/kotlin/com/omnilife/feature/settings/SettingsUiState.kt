package com.omnilife.feature.settings

import com.omnilife.core.sync.SyncState
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.ThemeMode
import kotlinx.datetime.LocalTime

/**
 * MVI state for SET-001 (Settings screen), restricted to the catalog entries this sprint's
 * Vertical Slice wires up end to end — see `domain-account`'s `SettingKey` doc for the full
 * scoping rationale (SET-R-01).
 */
public data class SettingsUiState(
    val isLoading: Boolean = true,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.INDACO,
    val notificationDailyBudget: Int = 3,
    val quietHoursStart: LocalTime = LocalTime(22, 0),
    val quietHoursEnd: LocalTime = LocalTime(8, 0),
    /** Null until the first [com.omnilife.core.sync.SyncStateManager] read completes. */
    val syncStatus: SyncState? = null,
    val errorMessage: String? = null,
    /** SET-R-04-adjacent confirmation gate: reset-onboarding is a real state change, never silent. */
    val onboardingResetConfirmationPending: Boolean = false,
    val onboardingWasReset: Boolean = false,
)
