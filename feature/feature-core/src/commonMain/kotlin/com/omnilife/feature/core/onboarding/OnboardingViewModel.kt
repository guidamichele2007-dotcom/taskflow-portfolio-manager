package com.omnilife.feature.core.onboarding

import com.omnilife.core.common.EntityId
import com.omnilife.core.common.onFailure
import com.omnilife.core.common.onSuccess
import com.omnilife.domain.account.SettingKey
import com.omnilife.domain.account.usecase.CompleteOnboarding
import com.omnilife.domain.account.usecase.UpdateSetting
import com.omnilife.domain.task.usecase.CreateTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI store for the onboarding flow (UX Bible §11, TDR-02). [listId]/[ownerAccountId]/[deviceId]
 * are resolved once by the composition root, same convention as [com.omnilife.feature.task.TaskCreateViewModel].
 * Notifies completion via [onCompleted] rather than a navigation type this module doesn't own —
 * the composition root decides what "onboarding is done" navigates to.
 */
public class OnboardingViewModel(
    private val updateSetting: UpdateSetting,
    private val completeOnboarding: CompleteOnboarding,
    private val createTask: CreateTask,
    private val listId: EntityId,
    private val ownerAccountId: String,
    private val deviceId: String,
    private val onCompleted: () -> Unit,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(OnboardingUiState())
    public val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    public fun dispatch(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.ContinueFromWelcome -> _state.update { it.copy(step = OnboardingStep.PERSONALIZE) }

            is OnboardingIntent.ChangeTheme -> {
                _state.update { it.copy(theme = intent.theme) }
                scope.launch { updateSetting(SettingKey.THEME, intent.theme.name) }
            }

            is OnboardingIntent.ChangeAccentColor -> {
                _state.update { it.copy(accentColor = intent.accentColor) }
                scope.launch { updateSetting(SettingKey.ACCENT_COLOR, intent.accentColor.name) }
            }

            OnboardingIntent.SkipPersonalize, OnboardingIntent.ContinueFromPersonalize ->
                _state.update { it.copy(step = OnboardingStep.FIRST_CAPTURE) }

            is OnboardingIntent.ChangeFirstTaskTitle ->
                _state.update { it.copy(firstTaskTitle = intent.title, errorMessage = null) }

            OnboardingIntent.CreateFirstTaskAndFinish -> createFirstTaskAndFinish()

            OnboardingIntent.SkipFirstCaptureAndFinish -> finish()
        }
    }

    private fun createFirstTaskAndFinish() {
        val current = _state.value
        if (!current.canCreateFirstTask) return
        _state.update { it.copy(isSaving = true) }
        scope.launch {
            createTask(current.firstTaskTitle, listId, ownerAccountId, deviceId)
                .onSuccess { finish() }
                .onFailure { error -> _state.update { it.copy(isSaving = false, errorMessage = error.message) } }
        }
    }

    private fun finish() {
        scope.launch {
            completeOnboarding()
            _state.update { it.copy(step = OnboardingStep.DONE, isSaving = false) }
            onCompleted()
        }
    }

    /** Cancels all in-flight work; call when the screen owning this store is disposed. */
    public fun clear() {
        scope.coroutineContext[Job]?.cancel()
    }
}
