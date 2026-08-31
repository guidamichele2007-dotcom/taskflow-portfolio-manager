package com.omnilife.feature.core.onboarding

import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.ThemeMode

/**
 * Onboarding Bible (`11-onboarding-experience.md`): minimal, no multi-screen tutorial (P115).
 * Three steps only — welcome, an OPTIONAL single skippable personalization step (theme + accent),
 * and a real first capture — never a guided tour.
 */
public enum class OnboardingStep {
    WELCOME,
    PERSONALIZE,
    FIRST_CAPTURE,
    DONE,
}

public data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.INDACO,
    val firstTaskTitle: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    public val canCreateFirstTask: Boolean get() = firstTaskTitle.isNotBlank() && !isSaving
}
