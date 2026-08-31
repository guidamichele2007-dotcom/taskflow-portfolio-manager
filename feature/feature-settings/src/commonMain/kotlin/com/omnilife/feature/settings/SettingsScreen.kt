package com.omnilife.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniButton
import com.omnilife.core.designsystem.components.OmniButtonVariant
import com.omnilife.core.designsystem.components.OmniChip
import com.omnilife.core.designsystem.components.OmniDialog
import com.omnilife.core.designsystem.components.OmniIconButton
import com.omnilife.core.designsystem.components.OmniSegmentedControl
import com.omnilife.core.designsystem.components.OmniTopBar
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.sync.SyncPhase
import com.omnilife.core.sync.SyncState
import com.omnilife.domain.account.AccentColor
import com.omnilife.domain.account.ThemeMode

private val THEMES = ThemeMode.entries
private val THEME_LABELS = listOf("Sistema", "Chiaro", "Scuro")
private val ACCENTS = AccentColor.entries

/** SET-001 (Settings). SET-R-02: every change applies immediately — no explicit "save" anywhere. */
@Composable
public fun SettingsScreen(
    state: SettingsUiState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        OmniTopBar(title = "Profilo")
        SectionTitle("Aspetto")
        OmniSegmentedControl(
            segments = THEME_LABELS,
            selectedIndex = THEMES.indexOf(state.theme),
            onSegmentSelected = { onIntent(SettingsIntent.ChangeTheme(THEMES[it])) },
            modifier = Modifier.padding(horizontal = OmniTheme.spacing.spazio2),
        )
        AccentColorRow(selected = state.accentColor, onSelect = { onIntent(SettingsIntent.ChangeAccentColor(it)) })

        SectionTitle("Notifiche")
        NotificationBudgetRow(
            budget = state.notificationDailyBudget,
            onChange = { onIntent(SettingsIntent.ChangeNotificationDailyBudget(it)) },
        )
        QuietHoursRow(start = state.quietHoursStart.toString(), end = state.quietHoursEnd.toString())

        SectionTitle("Dati")
        SyncStatusRow(state.syncStatus)

        SectionTitle("Sicurezza e privacy")
        ComingSoonRow(message = "Blocco biometrico, chiave di recupero e privacy arrivano in un prossimo sprint.")

        SectionTitle("Aiuto")
        ResetOnboardingRow(
            confirmationPending = state.onboardingResetConfirmationPending,
            onRequest = { onIntent(SettingsIntent.RequestResetOnboarding) },
            onConfirm = { onIntent(SettingsIntent.ConfirmResetOnboarding) },
            onCancel = { onIntent(SettingsIntent.CancelResetOnboarding) },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    BasicText(
        text = text,
        style = OmniTheme.typography.titoloSezione.copy(color = OmniTheme.colors.testoPrimario),
        modifier = Modifier.padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1),
    )
}

@Composable
private fun AccentColorRow(
    selected: AccentColor,
    onSelect: (AccentColor) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(OmniTheme.spacing.spazio2),
        horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1),
    ) {
        ACCENTS.forEach { accent ->
            OmniChip(text = accent.name, selected = accent == selected, onClick = { onSelect(accent) })
        }
    }
}

@Composable
private fun NotificationBudgetRow(
    budget: Int,
    onChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = OmniTheme.spacing.spazio2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = "Budget giornaliero: $budget",
            style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoPrimario),
        )
        Row {
            OmniIconButton(
                icon = OmniIconType.CLOSE,
                contentDescription = "Diminuisci",
                onClick = { if (budget > 0) onChange(budget - 1) },
            )
            OmniIconButton(
                icon = OmniIconType.ADD,
                contentDescription = "Aumenta",
                onClick = { if (budget < 10) onChange(budget + 1) },
            )
        }
    }
}

@Composable
private fun QuietHoursRow(
    start: String,
    end: String,
) {
    BasicText(
        text = "Silenzio: $start – $end",
        modifier = Modifier.padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1),
        style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoSecondario),
    )
}

@Composable
private fun SyncStatusRow(status: SyncState?) {
    if (status == null) return
    val label =
        when (status.phase) {
            SyncPhase.IDLE -> "Sincronizzato"
            SyncPhase.SYNCING -> "Sincronizzazione in corso"
            SyncPhase.OFFLINE -> "Offline — le modifiche si sincronizzeranno al ritorno online"
            SyncPhase.ERROR -> status.lastError ?: "Errore di sincronizzazione"
        }
    BasicText(
        text = "$label · ${status.pendingCount} in coda",
        modifier = Modifier.padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1),
        style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoSecondario),
    )
}

@Composable
private fun ComingSoonRow(message: String) {
    BasicText(
        text = message,
        modifier = Modifier.padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1),
        style = OmniTheme.typography.didascalia.copy(color = OmniTheme.colors.testoSecondario),
    )
}

@Composable
private fun ResetOnboardingRow(
    confirmationPending: Boolean,
    onRequest: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    OmniButton(
        text = "Ripeti l'introduzione",
        onClick = onRequest,
        variant = OmniButtonVariant.SECONDARIO,
        modifier = Modifier.padding(OmniTheme.spacing.spazio2),
    )
    if (confirmationPending) {
        OmniDialog(
            title = "Ripetere l'introduzione?",
            message = "Tornerai al primo avvio; le tue attività restano intatte.",
            onDismissRequest = onCancel,
            confirmLabel = "Ripeti",
            onConfirm = onConfirm,
            dismissLabel = "Annulla",
            onDismissClick = onCancel,
        )
    }
}
