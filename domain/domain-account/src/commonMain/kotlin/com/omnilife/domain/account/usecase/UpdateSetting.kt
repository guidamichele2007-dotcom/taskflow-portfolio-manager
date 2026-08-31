package com.omnilife.domain.account.usecase

import com.omnilife.core.common.OmniResult
import com.omnilife.core.eventbus.EventBus
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.Setting
import com.omnilife.domain.account.SettingError
import com.omnilife.domain.account.SettingEvent
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.SettingsRepository
import com.omnilife.domain.account.ThemeMode
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime

/**
 * SET-R-02: applies immediately and reversibly — a single upsert, no staged/pending state. Scope
 * is always [SettingKey.scope], never caller-supplied (SET-R-03 is a property of the catalog
 * entry, not a per-write choice). Publishes [SettingEvent.Updated] (Sprint 6) so a live UI surface
 * that read this setting at startup — MainActivity's theme/accent — can react without a restart.
 */
public class UpdateSetting(
    private val repository: SettingsRepository,
    private val eventBus: EventBus,
    private val clock: Clock = Clock.System,
) {
    public suspend operator fun invoke(
        key: SettingKey,
        value: String,
    ): OmniResult<Setting> {
        if (!isValid(key, value)) return OmniResult.Failure(SettingError.InvalidValue(key, value))
        val setting = Setting(key = key, value = value, scope = key.scope, modifiedAt = clock.now())
        repository.upsertSetting(setting)
        eventBus.publish(SettingEvent.Updated(key, value))
        return OmniResult.Success(setting)
    }

    private fun isValid(
        key: SettingKey,
        value: String,
    ): Boolean =
        when (key) {
            SettingKey.THEME -> runCatching { ThemeMode.valueOf(value) }.isSuccess
            SettingKey.ACCENT_COLOR -> runCatching { AccentColor.valueOf(value) }.isSuccess
            SettingKey.NOTIFICATION_DAILY_BUDGET -> value.toIntOrNull()?.let { it in 0..10 } ?: false
            SettingKey.NOTIFICATION_QUIET_HOURS_START,
            SettingKey.NOTIFICATION_QUIET_HOURS_END,
            -> runCatching { LocalTime.parse(value) }.isSuccess
        }
}
