package com.omnilife.domain.account.persistence

import com.omnilife.domain.account.OnboardingState
import com.omnilife.domain.account.Setting
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.SettingScope
import kotlinx.datetime.Instant

internal fun SettingEntity.toDomain(): Setting =
    Setting(
        key = SettingKey.valueOf(settingKey),
        value = settingValue,
        scope = SettingScope.valueOf(scope),
        modifiedAt = Instant.parse(modifiedAt),
    )

internal fun OnboardingStateEntity.toDomain(): OnboardingState =
    OnboardingState(
        completed = completed == 1L,
        completedAt = completedAt?.let { Instant.parse(it) },
    )
