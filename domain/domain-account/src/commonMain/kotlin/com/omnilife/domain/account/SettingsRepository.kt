package com.omnilife.domain.account

/**
 * Persistence port for Settings + onboarding state (Technical Architecture Bible §01 §4 — L3
 * depends only on abstractions it declares). Mirrors `domain-task`'s `TaskRepository` convention:
 * the concrete SQLDelight implementation lives in this same module (TDR-20).
 */
public interface SettingsRepository {
    /** Null if no row exists yet for [key] — the caller applies [SettingDefaults] in that case. */
    public suspend fun findSetting(key: SettingKey): Setting?

    public suspend fun findAllSettings(): List<Setting>

    public suspend fun upsertSetting(setting: Setting)

    public suspend fun findOnboardingState(): OnboardingState

    public suspend fun saveOnboardingState(state: OnboardingState)
}
