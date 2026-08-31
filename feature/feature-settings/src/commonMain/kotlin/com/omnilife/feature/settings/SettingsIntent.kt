package com.omnilife.feature.settings

import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.ThemeMode
import kotlinx.datetime.LocalTime

/** User intents for SET-001. Every change applies immediately (SET-R-02) — no explicit "save". */
public sealed interface SettingsIntent {
    public data object Load : SettingsIntent

    public data class ChangeTheme(val theme: ThemeMode) : SettingsIntent

    public data class ChangeAccentColor(val accentColor: AccentColor) : SettingsIntent

    public data class ChangeNotificationDailyBudget(val budget: Int) : SettingsIntent

    public data class ChangeQuietHours(val start: LocalTime, val end: LocalTime) : SettingsIntent

    /** First tap: arms [SettingsUiState.onboardingResetConfirmationPending]; a second confirms. */
    public data object RequestResetOnboarding : SettingsIntent

    public data object ConfirmResetOnboarding : SettingsIntent

    public data object CancelResetOnboarding : SettingsIntent
}
