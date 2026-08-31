package com.omnilife.domain.account.usecase

import com.omnilife.core.common.OmniResult
import com.omnilife.domain.account.SettingDefaults
import com.omnilife.domain.account.SettingError
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.SettingScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SettingsUseCasesTest {
    @Test
    fun `GetSettings fills in defaults for every catalog key when nothing is stored yet`() =
        runTest {
            val settings = GetSettings(FakeSettingsRepository())()

            assertEquals(SettingKey.entries.toSet(), settings.keys)
            assertEquals(SettingDefaults.THEME, settings.getValue(SettingKey.THEME).value)
            assertEquals(SettingDefaults.NOTIFICATION_DAILY_BUDGET, settings.getValue(SettingKey.NOTIFICATION_DAILY_BUDGET).value)
        }

    @Test
    fun `GetSettings prefers a stored value over the default`() =
        runTest {
            val repository = FakeSettingsRepository()
            UpdateSetting(repository)(SettingKey.THEME, "DARK")

            val settings = GetSettings(repository)()

            assertEquals("DARK", settings.getValue(SettingKey.THEME).value)
        }

    @Test
    fun `UpdateSetting persists a valid theme value with the key's canonical scope`() =
        runTest {
            val repository = FakeSettingsRepository()

            val result = UpdateSetting(repository)(SettingKey.THEME, "LIGHT")

            assertTrue(result is OmniResult.Success)
            val stored = repository.findSetting(SettingKey.THEME)
            assertNotNull(stored)
            assertEquals("LIGHT", stored.value)
            assertEquals(SettingScope.SYNCED_ACROSS_DEVICES, stored.scope)
        }

    @Test
    fun `UpdateSetting rejects a theme value outside the catalog's domain (SET-R-01)`() =
        runTest {
            val repository = FakeSettingsRepository()

            val result = UpdateSetting(repository)(SettingKey.THEME, "RAINBOW")

            assertEquals(SettingError.InvalidValue(SettingKey.THEME, "RAINBOW"), (result as OmniResult.Failure).error)
            assertEquals(null, repository.findSetting(SettingKey.THEME))
        }

    @Test
    fun `UpdateSetting rejects a notification budget outside 0 to 10`() =
        runTest {
            val repository = FakeSettingsRepository()

            val result = UpdateSetting(repository)(SettingKey.NOTIFICATION_DAILY_BUDGET, "11")

            assertTrue(result is OmniResult.Failure)
        }

    @Test
    fun `UpdateSetting rejects a malformed quiet-hours time`() =
        runTest {
            val repository = FakeSettingsRepository()

            val result = UpdateSetting(repository)(SettingKey.NOTIFICATION_QUIET_HOURS_START, "not-a-time")

            assertTrue(result is OmniResult.Failure)
        }

    @Test
    fun `CompleteOnboarding then ResetOnboarding round-trips through the repository`() =
        runTest {
            val repository = FakeSettingsRepository()
            assertFalse(GetOnboardingState(repository)().completed)

            val completed = CompleteOnboarding(repository)()
            assertTrue(completed.completed)
            assertNotNull(completed.completedAt)
            assertTrue(GetOnboardingState(repository)().completed)

            val reset = ResetOnboarding(repository)()
            assertFalse(reset.completed)
            assertEquals(null, reset.completedAt)
            assertFalse(GetOnboardingState(repository)().completed)
        }
}
