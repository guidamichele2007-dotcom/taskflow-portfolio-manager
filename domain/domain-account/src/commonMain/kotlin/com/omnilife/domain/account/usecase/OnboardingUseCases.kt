package com.omnilife.domain.account.usecase

import com.omnilife.domain.account.OnboardingState
import com.omnilife.domain.account.SettingsRepository
import kotlinx.datetime.Clock

/** Marks the minimal onboarding flow as done (UX Bible §11) — idempotent, no error path. */
public class CompleteOnboarding(private val repository: SettingsRepository, private val clock: Clock = Clock.System) {
    public suspend operator fun invoke(): OnboardingState {
        val state = OnboardingState(completed = true, completedAt = clock.now())
        repository.saveOnboardingState(state)
        return state
    }
}

/** Settings catalog action "reset onboarding" — lets the user re-run the flow deliberately. */
public class ResetOnboarding(private val repository: SettingsRepository) {
    public suspend operator fun invoke(): OnboardingState {
        val state = OnboardingState(completed = false, completedAt = null)
        repository.saveOnboardingState(state)
        return state
    }
}

public class GetOnboardingState(private val repository: SettingsRepository) {
    public suspend operator fun invoke(): OnboardingState = repository.findOnboardingState()
}
