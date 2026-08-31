package com.omnilife.feature.core.onboarding

import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.ThemeMode

/** User intents for the onboarding flow (UX Bible §11). */
public sealed interface OnboardingIntent {
    public data object ContinueFromWelcome : OnboardingIntent

    public data class ChangeTheme(val theme: ThemeMode) : OnboardingIntent

    public data class ChangeAccentColor(val accentColor: AccentColor) : OnboardingIntent

    /** P115: personalization is always skippable — instant defaults apply. */
    public data object SkipPersonalize : OnboardingIntent

    public data object ContinueFromPersonalize : OnboardingIntent

    public data class ChangeFirstTaskTitle(val title: String) : OnboardingIntent

    public data object CreateFirstTaskAndFinish : OnboardingIntent

    /** First capture is real but not mandatory to finish onboarding (UX Bible: no forced action). */
    public data object SkipFirstCaptureAndFinish : OnboardingIntent
}
