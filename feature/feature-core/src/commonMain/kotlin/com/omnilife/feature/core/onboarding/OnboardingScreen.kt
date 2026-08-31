package com.omnilife.feature.core.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniButton
import com.omnilife.core.designsystem.components.OmniButtonVariant
import com.omnilife.core.designsystem.components.OmniChip
import com.omnilife.core.designsystem.components.OmniSegmentedControl
import com.omnilife.core.designsystem.components.OmniTextField
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.ThemeMode

private val THEMES = ThemeMode.entries
private val THEME_LABELS = listOf("Sistema", "Chiaro", "Scuro")
private val ACCENTS = AccentColor.entries

/**
 * Onboarding Bible §11: minimal, no multi-screen tutorial (P115). Three steps, one screen each —
 * never a guided tour.
 */
@Composable
public fun OnboardingScreen(
    state: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(OmniTheme.spacing.spazio3)) {
        when (state.step) {
            OnboardingStep.WELCOME -> WelcomeStep(onContinue = { onIntent(OnboardingIntent.ContinueFromWelcome) })
            OnboardingStep.PERSONALIZE ->
                PersonalizeStep(
                    state = state,
                    onIntent = onIntent,
                )

            OnboardingStep.FIRST_CAPTURE -> FirstCaptureStep(state = state, onIntent = onIntent)
            OnboardingStep.DONE -> Unit
        }
    }
}

@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = "Benvenuto in OmniLife",
            style = OmniTheme.typography.titoloSchermata.copy(color = OmniTheme.colors.testoPrimario),
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio1))
        BasicText(
            text = "Le tue attività, sempre con te — anche offline.",
            style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoSecondario),
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio3))
        OmniButton(text = "Inizia", onClick = onContinue)
    }
}

@Composable
private fun PersonalizeStep(
    state: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2)) {
        BasicText(
            text = "Personalizza (facoltativo)",
            style = OmniTheme.typography.titoloSezione.copy(color = OmniTheme.colors.testoPrimario),
        )
        OmniSegmentedControl(
            segments = THEME_LABELS,
            selectedIndex = THEMES.indexOf(state.theme),
            onSegmentSelected = { onIntent(OnboardingIntent.ChangeTheme(THEMES[it])) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1),
        ) {
            ACCENTS.forEach { accent ->
                OmniChip(
                    text = accent.name,
                    selected = accent == state.accentColor,
                    onClick = { onIntent(OnboardingIntent.ChangeAccentColor(accent)) },
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2)) {
            OmniButton(
                text = "Salta",
                onClick = { onIntent(OnboardingIntent.SkipPersonalize) },
                variant = OmniButtonVariant.TESTUALE,
            )
            OmniButton(text = "Continua", onClick = { onIntent(OnboardingIntent.ContinueFromPersonalize) })
        }
    }
}

@Composable
private fun FirstCaptureStep(
    state: OnboardingUiState,
    onIntent: (OnboardingIntent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2)) {
        BasicText(
            text = "La tua prima attività",
            style = OmniTheme.typography.titoloSezione.copy(color = OmniTheme.colors.testoPrimario),
        )
        OmniTextField(
            value = state.firstTaskTitle,
            onValueChange = { onIntent(OnboardingIntent.ChangeFirstTaskTitle(it)) },
            placeholder = "Cosa devi fare?",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2)) {
            OmniButton(
                text = "Salta",
                onClick = { onIntent(OnboardingIntent.SkipFirstCaptureAndFinish) },
                variant = OmniButtonVariant.TESTUALE,
            )
            OmniButton(
                text = "Crea e inizia",
                onClick = { onIntent(OnboardingIntent.CreateFirstTaskAndFinish) },
                enabled = state.canCreateFirstTask,
                loading = state.isSaving,
            )
        }
    }
}
