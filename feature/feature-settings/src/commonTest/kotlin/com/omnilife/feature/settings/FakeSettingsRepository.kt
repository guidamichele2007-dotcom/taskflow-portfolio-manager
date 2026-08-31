package com.omnilife.feature.settings

import com.omnilife.domain.account.OnboardingState
import com.omnilife.domain.account.Setting
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.SettingsRepository

/** In-memory [SettingsRepository] for ViewModel tests — mirrors `domain-account`'s own test fake. */
internal class FakeSettingsRepository : SettingsRepository {
    val settings = mutableMapOf<SettingKey, Setting>()
    var onboardingState = OnboardingState(completed = false, completedAt = null)

    override suspend fun findSetting(key: SettingKey): Setting? = settings[key]

    override suspend fun findAllSettings(): List<Setting> = settings.values.toList()

    override suspend fun upsertSetting(setting: Setting) {
        settings[setting.key] = setting
    }

    override suspend fun findOnboardingState(): OnboardingState = onboardingState

    override suspend fun saveOnboardingState(state: OnboardingState) {
        onboardingState = state
    }
}
