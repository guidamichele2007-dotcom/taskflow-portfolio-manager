package com.omnilife.feature.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniButton
import com.omnilife.core.designsystem.components.OmniButtonVariant
import com.omnilife.core.designsystem.components.OmniCard
import com.omnilife.core.designsystem.components.OmniEmptyState
import com.omnilife.core.designsystem.components.OmniErrorState
import com.omnilife.core.designsystem.components.OmniListItem
import com.omnilife.core.designsystem.components.OmniLoadingState
import com.omnilife.core.designsystem.components.OmniSearchField
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.notifications.NotificationRequest
import com.omnilife.core.sync.SyncPhase
import com.omnilife.core.sync.SyncState

/** Sync Status (HOME-007's counterpart on the sync side: always-reactive, no manual refresh action). */
@Composable
internal fun SyncStatusRow(
    status: SyncState?,
    modifier: Modifier = Modifier,
) {
    if (status == null) return
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OmniIcon(type = OmniIconType.SYNC, contentDescription = null, tint = syncTint(status.phase))
        Spacer(Modifier.size(OmniTheme.spacing.spazio1))
        BasicText(
            text = syncLabel(status),
            style = OmniTheme.typography.didascalia.copy(color = OmniTheme.colors.testoSecondario),
        )
    }
}

private fun syncLabel(status: SyncState): String =
    when (status.phase) {
        SyncPhase.IDLE -> "Sincronizzato"
        SyncPhase.SYNCING -> "Sincronizzazione in corso"
        SyncPhase.OFFLINE -> "Offline — le modifiche si sincronizzeranno al ritorno online"
        SyncPhase.ERROR -> status.lastError ?: "Errore di sincronizzazione"
    }

@Composable
private fun syncTint(phase: SyncPhase) =
    when (phase) {
        SyncPhase.ERROR -> OmniTheme.colors.statoCritico
        SyncPhase.OFFLINE -> OmniTheme.colors.statoAttenzione
        else -> OmniTheme.colors.testoSecondario
    }

/** Global Search Entry: incremental, no submit button (SRCH-001), backed by `core-search`. */
@Composable
internal fun GlobalSearchEntry(
    query: String,
    resultCount: Int?,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OmniSearchField(
        query = query,
        onQueryChange = onQueryChange,
        modifier = modifier.fillMaxWidth().padding(OmniTheme.spacing.spazio2),
        placeholder = "Cerca in tutti i moduli",
        resultCount = resultCount,
    )
}

/** Notification Center: NTF-007's in-app panel, backed directly by `core-notifications`' history. */
@Composable
internal fun NotificationCenterPanel(
    summary: HomeNotificationSummary,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OmniCard(title = "Notifiche", modifier = modifier.padding(horizontal = OmniTheme.spacing.spazio2)) {
        if (summary.recent.isEmpty()) {
            OmniEmptyState(
                icon = OmniIconType.NOTIFICATIONS,
                message = "Nessuna notifica",
                actionLabel = "Chiudi",
                onActionClick = {},
            )
        } else {
            summary.recent.take(NOTIFICATION_PREVIEW_LIMIT).forEach { request ->
                NotificationRow(request, onClick = { onDismiss(request.id) })
            }
        }
    }
}

@Composable
private fun NotificationRow(
    request: NotificationRequest,
    onClick: () -> Unit,
) {
    OmniListItem(title = request.title, secondaryText = request.body, onClick = onClick)
}

private const val NOTIFICATION_PREVIEW_LIMIT = 5

/** Quick Actions (HOME-004): 1-tap, no screen change. Handlers are placeholders this sprint — see HomeViewModel. */
@Composable
internal fun QuickActionsRow(
    actions: List<HomeQuickAction>,
    onQuickAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = OmniTheme.spacing.spazio2),
        horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1),
    ) {
        actions.forEach { action ->
            OmniButton(
                text = action.label,
                onClick = { onQuickAction(action.id) },
                variant = OmniButtonVariant.SECONDARIO,
            )
        }
    }
}

/** One Home widget card (Widget System, HOME-002), rendering whichever [HomeSectionState] it's currently in. */
@Composable
internal fun HomeWidgetCard(
    kind: HomeWidgetKind,
    sectionState: HomeSectionState<List<HomeListEntry>>,
    modifier: Modifier = Modifier,
) {
    OmniCard(title = widgetTitle(kind), modifier = modifier.padding(horizontal = OmniTheme.spacing.spazio2)) {
        when (sectionState) {
            is HomeSectionState.Loading -> OmniLoadingState()
            is HomeSectionState.Error ->
                OmniErrorState(message = sectionState.message, actionLabel = "Riprova", onActionClick = {})
            is HomeSectionState.Empty ->
                OmniEmptyState(
                    icon = OmniIconType.INFO,
                    message = sectionState.message,
                    actionLabel = "Scopri di più",
                    onActionClick = {},
                )
            is HomeSectionState.Content ->
                sectionState.data.forEach { entry ->
                    OmniListItem(title = entry.title, secondaryText = entry.secondaryText, completed = entry.completed)
                }
        }
    }
}

private fun widgetTitle(kind: HomeWidgetKind): String =
    when (kind) {
        HomeWidgetKind.TODAY_OVERVIEW -> "Oggi"
        HomeWidgetKind.AGENDA -> "Agenda"
        HomeWidgetKind.GOAL_SUMMARY -> "Obiettivi"
        HomeWidgetKind.HABIT_SUMMARY -> "Abitudini"
        HomeWidgetKind.FINANCE_SUMMARY -> "Finanze"
        HomeWidgetKind.CALENDAR_SUMMARY -> "Calendario"
        HomeWidgetKind.RECENT_ACTIVITY -> "Attività recente"
    }
