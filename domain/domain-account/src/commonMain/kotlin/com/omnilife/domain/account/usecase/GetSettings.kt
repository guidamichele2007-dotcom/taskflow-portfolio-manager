package com.omnilife.domain.account.usecase

import com.omnilife.domain.account.Setting
import com.omnilife.domain.account.SettingDefaults
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.SettingsRepository
import kotlinx.datetime.Instant

/**
 * Reads the full closed catalog (SET §2), filling in [SettingDefaults] for any [SettingKey] that
 * has no row yet — SET-R-02 ("ogni impostazione ha effetto immediato") requires every key to
 * always resolve to *some* value, never an absent/unknown state the UI must special-case.
 */
public class GetSettings(private val repository: SettingsRepository) {
    public suspend operator fun invoke(): Map<SettingKey, Setting> {
        val stored = repository.findAllSettings().associateBy { it.key }
        return SettingKey.entries.associateWith { key ->
            stored[key] ?: Setting(key = key, value = SettingDefaults.forKey(key), modifiedAt = Instant.DISTANT_PAST)
        }
    }
}
