package com.omnilife.domain.account

import kotlinx.datetime.Instant

/**
 * Whether the minimal onboarding flow (UX Bible, `11-onboarding-experience.md`) has been
 * completed on this account. Not part of the DM-SYS-06 Setting catalog — SET-R-01 keeps that
 * catalog closed to genuine user-facing preferences, and "has onboarding run" is app/account
 * lifecycle state, not a preference the user chose. Documented as TDR-36 (a small, genuinely
 * necessary undocumented decision, per this sprint's rule 8).
 */
public data class OnboardingState(
    val completed: Boolean,
    val completedAt: Instant?,
)
