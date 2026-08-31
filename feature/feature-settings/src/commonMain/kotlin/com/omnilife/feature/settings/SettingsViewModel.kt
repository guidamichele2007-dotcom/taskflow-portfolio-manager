package com.omnilife.feature.settings

import com.omnilife.core.sync.SyncStateManager
import com.omnilife.core.sync.SyncStateSubscription
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.ThemeMode
import com.omnilife.domain.account.usecase.GetSettings
import com.omnilife.domain.account.usecase.ResetOnboarding
import com.omnilife.domain.account.usecase.UpdateSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

/**
 * MVI store for SET-001 (TDR-02). Composes `domain-account`'s Setting use cases with
 * `core-sync`'s [SyncStateManager] for the read-only sync-status section (SET §2 "Dati" /
 * this sprint's "sync settings" scope) — never mutates sync state itself.
 */
public class SettingsViewModel(
    private val getSettings: GetSettings,
    private val updateSetting: UpdateSetting,
    private val resetOnboarding: ResetOnboarding,
    private val syncStateManager: SyncStateManager,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(SettingsUiState())
    public val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val syncStateSubscription: SyncStateSubscription

    init {
        _state.update { it.copy(syncStatus = syncStateManager.current()) }
        syncStateSubscription =
            syncStateManager.observe { newState -> _state.update { it.copy(syncStatus = newState) } }
        dispatch(SettingsIntent.Load)
    }

    public fun dispatch(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Load -> scope.launch { load() }

            is SettingsIntent.ChangeTheme ->
                scope.launch {
                    updateSetting(SettingKey.THEME, intent.theme.name)
                    load()
                }

            is SettingsIntent.ChangeAccentColor ->
                scope.launch {
                    updateSetting(SettingKey.ACCENT_COLOR, intent.accentColor.name)
                    load()
                }

            is SettingsIntent.ChangeNotificationDailyBudget ->
                scope.launch {
                    updateSetting(SettingKey.NOTIFICATION_DAILY_BUDGET, intent.budget.toString())
                    load()
                }

            is SettingsIntent.ChangeQuietHours ->
                scope.launch {
                    updateSetting(SettingKey.NOTIFICATION_QUIET_HOURS_START, intent.start.toString())
                    updateSetting(SettingKey.NOTIFICATION_QUIET_HOURS_END, intent.end.toString())
                    load()
                }

            SettingsIntent.RequestResetOnboarding ->
                _state.update { it.copy(onboardingResetConfirmationPending = true) }

            SettingsIntent.CancelResetOnboarding ->
                _state.update { it.copy(onboardingResetConfirmationPending = false) }

            SettingsIntent.ConfirmResetOnboarding ->
                scope.launch {
                    resetOnboarding()
                    _state.update {
                        it.copy(onboardingResetConfirmationPending = false, onboardingWasReset = true)
                    }
                }
        }
    }

    private suspend fun load() {
        val settings = getSettings()
        _state.update {
            it.copy(
                isLoading = false,
                theme = ThemeMode.valueOf(settings.getValue(SettingKey.THEME).value),
                accentColor = AccentColor.valueOf(settings.getValue(SettingKey.ACCENT_COLOR).value),
                notificationDailyBudget = settings.getValue(SettingKey.NOTIFICATION_DAILY_BUDGET).value.toInt(),
                quietHoursStart = LocalTime.parse(settings.getValue(SettingKey.NOTIFICATION_QUIET_HOURS_START).value),
                quietHoursEnd = LocalTime.parse(settings.getValue(SettingKey.NOTIFICATION_QUIET_HOURS_END).value),
                errorMessage = null,
            )
        }
    }

    /** Cancels all in-flight work and the sync-state listener; call when this store is disposed. */
    public fun clear() {
        syncStateSubscription.cancel()
        scope.coroutineContext[Job]?.cancel()
    }
}
