package com.omnilife.feature.core.onboarding

import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.ThemeMode
import com.omnilife.domain.account.usecase.CompleteOnboarding
import com.omnilife.domain.account.usecase.UpdateSetting
import com.omnilife.domain.task.usecase.CreateTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private fun viewModel(
        settingsRepository: FakeSettingsRepository = FakeSettingsRepository(),
        taskRepository: FakeTaskRepository = FakeTaskRepository(),
        onCompleted: () -> Unit = {},
    ) = OnboardingViewModel(
        updateSetting = UpdateSetting(settingsRepository),
        completeOnboarding = CompleteOnboarding(settingsRepository),
        createTask = CreateTask(taskRepository, InMemoryEventBus(), newId = { "task-1" }),
        listId = "list-1",
        ownerAccountId = "acc-1",
        deviceId = "dev-1",
        onCompleted = onCompleted,
        scope = CoroutineScope(UnconfinedTestDispatcher()),
    )

    @Test
    fun `starts on the welcome step`() {
        assertEquals(OnboardingStep.WELCOME, viewModel().state.value.step)
    }

    @Test
    fun `continuing from welcome moves to the personalize step`() {
        val viewModel = viewModel()
        viewModel.dispatch(OnboardingIntent.ContinueFromWelcome)
        assertEquals(OnboardingStep.PERSONALIZE, viewModel.state.value.step)
    }

    @Test
    fun `changing theme during personalize persists it immediately`() =
        runTest {
            val settingsRepository = FakeSettingsRepository()
            val viewModel = viewModel(settingsRepository)
            viewModel.dispatch(OnboardingIntent.ContinueFromWelcome)

            viewModel.dispatch(OnboardingIntent.ChangeTheme(ThemeMode.DARK))

            assertEquals(ThemeMode.DARK, viewModel.state.value.theme)
            assertEquals("DARK", settingsRepository.settings.getValue(SettingKey.THEME).value)
        }

    @Test
    fun `changing accent color during personalize persists it immediately`() =
        runTest {
            val settingsRepository = FakeSettingsRepository()
            val viewModel = viewModel(settingsRepository)
            viewModel.dispatch(OnboardingIntent.ContinueFromWelcome)

            viewModel.dispatch(OnboardingIntent.ChangeAccentColor(AccentColor.VERDE))

            assertEquals(AccentColor.VERDE, viewModel.state.value.accentColor)
        }

    @Test
    fun `personalize is always skippable and still advances to first capture (P115)`() {
        val viewModel = viewModel()
        viewModel.dispatch(OnboardingIntent.ContinueFromWelcome)

        viewModel.dispatch(OnboardingIntent.SkipPersonalize)

        assertEquals(OnboardingStep.FIRST_CAPTURE, viewModel.state.value.step)
    }

    @Test
    fun `creating a real first task marks onboarding complete and invokes onCompleted`() =
        runTest {
            val settingsRepository = FakeSettingsRepository()
            val taskRepository = FakeTaskRepository()
            var completedCalls = 0
            val viewModel = viewModel(settingsRepository, taskRepository, onCompleted = { completedCalls++ })
            viewModel.dispatch(OnboardingIntent.ContinueFromWelcome)
            viewModel.dispatch(OnboardingIntent.SkipPersonalize)
            viewModel.dispatch(OnboardingIntent.ChangeFirstTaskTitle("Plan the week"))

            viewModel.dispatch(OnboardingIntent.CreateFirstTaskAndFinish)

            assertEquals("Plan the week", taskRepository.tasks.getValue("task-1").title)
            assertTrue(settingsRepository.onboardingState.completed)
            assertEquals(OnboardingStep.DONE, viewModel.state.value.step)
            assertEquals(1, completedCalls)
        }

    @Test
    fun `skipping first capture still marks onboarding complete, never invents a fake task`() =
        runTest {
            val settingsRepository = FakeSettingsRepository()
            val taskRepository = FakeTaskRepository()
            val viewModel = viewModel(settingsRepository, taskRepository)
            viewModel.dispatch(OnboardingIntent.ContinueFromWelcome)
            viewModel.dispatch(OnboardingIntent.SkipPersonalize)

            viewModel.dispatch(OnboardingIntent.SkipFirstCaptureAndFinish)

            assertTrue(taskRepository.tasks.isEmpty())
            assertTrue(settingsRepository.onboardingState.completed)
            assertEquals(OnboardingStep.DONE, viewModel.state.value.step)
        }

    @Test
    fun `CreateFirstTaskAndFinish is a no-op while the title is blank`() =
        runTest {
            val taskRepository = FakeTaskRepository()
            val viewModel = viewModel(taskRepository = taskRepository)
            viewModel.dispatch(OnboardingIntent.ContinueFromWelcome)
            viewModel.dispatch(OnboardingIntent.SkipPersonalize)

            viewModel.dispatch(OnboardingIntent.CreateFirstTaskAndFinish)

            assertTrue(taskRepository.tasks.isEmpty())
            assertFalse(viewModel.state.value.step == OnboardingStep.DONE)
        }
}
