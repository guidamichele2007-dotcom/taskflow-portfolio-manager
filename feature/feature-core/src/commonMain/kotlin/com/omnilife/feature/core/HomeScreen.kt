package com.omnilife.feature.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniTopBar
import com.omnilife.core.designsystem.components.OmniTopBarAction
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * Home "Oggi" (HOME-001…008). Stateless: the caller supplies [state] and wires [onIntent] to a
 * `HomeViewModel` — no ViewModel reference here, matching the L1/L2 boundary (Technical
 * Architecture Bible §01 §4: L1 never contains business logic). No pull-to-refresh gesture
 * anywhere in this tree (HOME-007).
 */
@Composable
public fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OmniTopBar(
            title = "Oggi",
            actions =
                listOf(
                    OmniTopBarAction(
                        icon = OmniIconType.NOTIFICATIONS,
                        contentDescription = notificationActionLabel(state.notificationSummary.pendingCount),
                        onClick = { onIntent(HomeIntent.ToggleNotificationCenter) },
                    ),
                ),
        )
        SyncStatusRow(state.syncStatus)
        GlobalSearchEntry(
            query = state.searchQuery,
            resultCount = if (state.searchQuery.isNotBlank()) state.searchResults.size else null,
            onQueryChange = { onIntent(HomeIntent.Search(it)) },
        )
        if (state.notificationCenterOpen) {
            NotificationCenterPanel(
                summary = state.notificationSummary,
                onDismiss = { onIntent(HomeIntent.DismissNotification(it)) },
            )
        }
        QuickActionsRow(
            actions = state.quickActions,
            onQuickAction = { onIntent(HomeIntent.PerformQuickAction(it)) },
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = OmniTheme.spacing.spazio2),
            verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2),
        ) {
            state.widgetOrder.forEach { kind ->
                HomeWidgetCard(kind = kind, sectionState = state.sections[kind] ?: HomeSectionState.Loading)
            }
        }
    }
}

private fun notificationActionLabel(pendingCount: Int): String =
    if (pendingCount > 0) "Notifiche, $pendingCount in attesa" else "Notifiche"
