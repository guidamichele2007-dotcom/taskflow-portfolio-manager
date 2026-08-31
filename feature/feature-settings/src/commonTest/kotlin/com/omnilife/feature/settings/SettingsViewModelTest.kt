package com.omnilife.feature.settings

import com.omnilife.core.sync.InMemorySyncStateManager
import com.omnilife.core.sync.SyncPhase
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.OnboardingState
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.ThemeMode
import com.omnilife.domain.account.usecase.GetSettings
import com.omnilife.domain.account.usecase.ResetOnboarding
import com.omnilife.domain.account.usecase.UpdateSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private fun viewModel(
        repository: FakeSettingsRepository = FakeSettingsRepository(),
        syncStateManager: InMemorySyncStateManager = InMemorySyncStateManager(),
    ) = SettingsViewModel(
        getSettings = GetSettings(repository),
        updateSetting = UpdateSetting(repository),
        resetOnboarding = ResetOnboarding(repository),
        syncStateManager = syncStateManager,
        scope = CoroutineScope(UnconfinedTestDispatcher()),
    )

    @Test
    fun `initial state loads catalog defaults when nothing is stored yet`() {
        val state = viewModel().state.value

        assertEquals(ThemeMode.SYSTEM, state.theme)
        assertEquals(AccentColor.INDACO, state.accentColor)
        assertEquals(3, state.notificationDailyBudget)
        assertEquals(LocalTime(22, 0), state.quietHoursStart)
        assertFalse(state.isLoading)
    }

    @Test
    fun `changing the theme persists it and updates state immediately (SET-R-02)`() =
        runTest {
            val repository = FakeSettingsRepository()
            val viewModel = viewModel(repository)

            viewModel.dispatch(SettingsIntent.ChangeTheme(ThemeMode.DARK))

            assertEquals(ThemeMode.DARK, viewModel.state.value.theme)
            assertEquals("DARK", repository.settings.getValue(SettingKey.THEME).value)
        }

    @Test
    fun `changing the accent color persists it`() =
        runTest {
            val viewModel = viewModel()

            viewModel.dispatch(SettingsIntent.ChangeAccentColor(AccentColor.CORALLO))

            assertEquals(AccentColor.CORALLO, viewModel.state.value.accentColor)
        }

    @Test
    fun `changing quiet hours updates both start and end`() =
        runTest {
            val viewModel = viewModel()

            viewModel.dispatch(SettingsIntent.ChangeQuietHours(LocalTime(23, 0), LocalTime(7, 0)))

            assertEquals(LocalTime(23, 0), viewModel.state.value.quietHoursStart)
            assertEquals(LocalTime(7, 0), viewModel.state.value.quietHoursEnd)
        }

    @Test
    fun `reset onboarding requires an explicit confirmation, never resets on the first tap alone`() =
        runTest {
            val repository = FakeSettingsRepository()
            repository.onboardingState = OnboardingState(true, null)
            val viewModel = viewModel(repository)

            viewModel.dispatch(SettingsIntent.RequestResetOnboarding)

            assertTrue(viewModel.state.value.onboardingResetConfirmationPending)
            assertFalse(viewModel.state.value.onboardingWasReset)
            assertTrue(repository.onboardingState.completed)
        }

    @Test
    fun `confirming reset onboarding actually resets it`() =
        runTest {
            val repository = FakeSettingsRepository()
            repository.onboardingState = OnboardingState(true, null)
            val viewModel = viewModel(repository)
            viewModel.dispatch(SettingsIntent.RequestResetOnboarding)

            viewModel.dispatch(SettingsIntent.ConfirmResetOnboarding)

            assertFalse(repository.onboardingState.completed)
            assertTrue(viewModel.state.value.onboardingWasReset)
            assertFalse(viewModel.state.value.onboardingResetConfirmationPending)
        }

    @Test
    fun `cancelling reset onboarding leaves onboarding state untouched`() =
        runTest {
            val repository = FakeSettingsRepository()
            repository.onboardingState = OnboardingState(true, null)
            val viewModel = viewModel(repository)
            viewModel.dispatch(SettingsIntent.RequestResetOnboarding)

            viewModel.dispatch(SettingsIntent.CancelResetOnboarding)

            assertFalse(viewModel.state.value.onboardingResetConfirmationPending)
            assertTrue(repository.onboardingState.completed)
        }

    @Test
    fun `sync status reflects the real SyncStateManager and updates on change`() =
        runTest {
            val syncStateManager = InMemorySyncStateManager()
            val viewModel = viewModel(syncStateManager = syncStateManager)
            assertEquals(SyncPhase.IDLE, viewModel.state.value.syncStatus?.phase)

            syncStateManager.transitionTo(SyncPhase.SYNCING)

            assertEquals(SyncPhase.SYNCING, viewModel.state.value.syncStatus?.phase)
        }

    @Test
    fun `clear unsubscribes from SyncStateManager`() =
        runTest {
            val syncStateManager = InMemorySyncStateManager()
            val viewModel = viewModel(syncStateManager = syncStateManager)

            viewModel.clear()
            syncStateManager.transitionTo(SyncPhase.ERROR)

            assertEquals(SyncPhase.IDLE, viewModel.state.value.syncStatus?.phase)
        }
}
