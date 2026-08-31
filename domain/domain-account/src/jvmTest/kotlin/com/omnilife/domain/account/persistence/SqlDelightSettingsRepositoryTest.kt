package com.omnilife.domain.account.persistence

import com.omnilife.domain.account.OnboardingState
import com.omnilife.domain.account.Setting
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.SettingScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Integration test (Engineering Plan T6 / IT): exercises the real SQLDelight schema against an
 * in-memory JVM SQLite database — same convention as `domain-task`'s
 * `SqlDelightTaskRepositoryTest`.
 */
class SqlDelightSettingsRepositoryTest {
    private lateinit var repository: SqlDelightSettingsRepository

    @BeforeTest
    fun setUp() {
        repository = SqlDelightSettingsRepository(DatabaseDriverFactory().createDriver())
    }

    @Test
    fun `a setting round-trips through upsert and findByKey unchanged`() =
        runTest {
            val setting =
                Setting(
                    key = SettingKey.THEME,
                    value = "DARK",
                    scope = SettingScope.SYNCED_ACROSS_DEVICES,
                    modifiedAt = Instant.fromEpochMilliseconds(0),
                )

            repository.upsertSetting(setting)

            assertEquals(setting, repository.findSetting(SettingKey.THEME))
        }

    @Test
    fun `upserting the same key twice replaces the previous value, never duplicates it`() =
        runTest {
            repository.upsertSetting(
                Setting(SettingKey.THEME, "LIGHT", modifiedAt = Instant.fromEpochMilliseconds(0)),
            )
            repository.upsertSetting(
                Setting(SettingKey.THEME, "DARK", modifiedAt = Instant.fromEpochMilliseconds(1)),
            )

            val all = repository.findAllSettings()

            assertEquals(1, all.count { it.key == SettingKey.THEME })
            assertEquals("DARK", repository.findSetting(SettingKey.THEME)?.value)
        }

    @Test
    fun `a key with no stored row returns null, letting the caller apply the catalog default`() =
        runTest {
            assertNull(repository.findSetting(SettingKey.ACCENT_COLOR))
        }

    @Test
    fun `onboarding state defaults to not completed before anything is saved`() =
        runTest {
            val state = repository.findOnboardingState()
            assertFalse(state.completed)
            assertNull(state.completedAt)
        }

    @Test
    fun `saving onboarding state persists and is readable back, surviving a fresh repository instance`() =
        runTest {
            val now = Instant.fromEpochMilliseconds(1_700_000_000_000)
            repository.saveOnboardingState(OnboardingState(completed = true, completedAt = now))

            assertTrue(repository.findOnboardingState().completed)
            assertEquals(now, repository.findOnboardingState().completedAt)
        }

    @Test
    fun `findAllSettings reflects every distinct key stored so far`() =
        runTest {
            repository.upsertSetting(Setting(SettingKey.THEME, "SYSTEM", modifiedAt = Instant.fromEpochMilliseconds(0)))
            repository.upsertSetting(Setting(SettingKey.ACCENT_COLOR, "CORALLO", modifiedAt = Instant.fromEpochMilliseconds(0)))

            val keys = repository.findAllSettings().map { it.key }.toSet()

            assertEquals(setOf(SettingKey.THEME, SettingKey.ACCENT_COLOR), keys)
        }
}
