package com.omnilife.domain.account.persistence

import app.cash.sqldelight.db.SqlDriver
import com.omnilife.domain.account.OnboardingState
import com.omnilife.domain.account.Setting
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** SQLDelight-backed [SettingsRepository] (TDR-20), same convention as `domain-task`'s. */
public class SqlDelightSettingsRepository(driver: SqlDriver) : SettingsRepository {
    private val database = AccountDatabase(driver)
    private val queries = database.accountQueries

    override suspend fun findSetting(key: SettingKey): Setting? =
        withContext(Dispatchers.Default) {
            queries.selectSettingByKey(key.name).executeAsOneOrNull()?.toDomain()
        }

    override suspend fun findAllSettings(): List<Setting> =
        withContext(Dispatchers.Default) {
            queries.selectAllSettings().executeAsList().map { it.toDomain() }
        }

    override suspend fun upsertSetting(setting: Setting): Unit =
        withContext(Dispatchers.Default) {
            queries.insertOrReplaceSetting(
                setting.key.name,
                setting.value,
                setting.scope.name,
                setting.modifiedAt.toString(),
            )
        }

    override suspend fun findOnboardingState(): OnboardingState =
        withContext(Dispatchers.Default) {
            queries.selectOnboardingState().executeAsOneOrNull()?.toDomain()
                ?: OnboardingState(completed = false, completedAt = null)
        }

    override suspend fun saveOnboardingState(state: OnboardingState): Unit =
        withContext(Dispatchers.Default) {
            queries.insertOrReplaceOnboardingState(
                if (state.completed) 1L else 0L,
                state.completedAt?.toString(),
            )
        }
}
