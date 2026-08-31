package com.omnilife.feature.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omnilife.core.common.EntityId
import com.omnilife.core.designsystem.components.OmniChip
import com.omnilife.core.designsystem.components.OmniEmptyState
import com.omnilife.core.designsystem.components.OmniFab
import com.omnilife.core.designsystem.components.OmniListItem
import com.omnilife.core.designsystem.components.OmniLoadingState
import com.omnilife.core.designsystem.components.OmniSearchField
import com.omnilife.core.designsystem.components.OmniSegmentedControl
import com.omnilife.core.designsystem.components.OmniTopBar
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.domain.task.Task
import com.omnilife.domain.task.usecase.TaskListMode
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

private val MODES = TaskListMode.entries
private val MODE_LABELS = listOf("Oggi", "Prossimi", "Tutte", "In sospeso")

/**
 * IA-030/031/032/034 (Task list — TASK-012/014). Stateless, mirrors [com.omnilife.feature.core.HomeScreen]'s
 * L1/L2 boundary: no `TaskListViewModel` reference, only [state]/[onIntent].
 */
@Composable
public fun TaskListScreen(
    state: TaskListUiState,
    onIntent: (TaskListIntent) -> Unit,
    onTaskClick: (EntityId) -> Unit,
    onCapture: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            OmniTopBar(title = "Attività")
            OmniSegmentedControl(
                segments = MODE_LABELS,
                selectedIndex = MODES.indexOf(state.mode),
                onSegmentSelected = { index -> onIntent(TaskListIntent.ChangeMode(MODES[index])) },
                modifier = Modifier.padding(OmniTheme.spacing.spazio2),
            )
            OmniSearchField(
                query = state.searchQuery,
                onQueryChange = { onIntent(TaskListIntent.Search(it)) },
                modifier = Modifier.padding(horizontal = OmniTheme.spacing.spazio2),
                placeholder = "Cerca nelle attività",
                resultCount = if (state.searchQuery.isNotBlank()) state.tasks.size else null,
            )
            TaskListContent(state = state, onIntent = onIntent, onTaskClick = onTaskClick, onCapture = onCapture)
        }
        OmniFab(
            onClick = onCapture,
            modifier = Modifier.align(Alignment.BottomEnd).padding(OmniTheme.spacing.spazio3),
        )
        val pendingChoiceTaskId = state.pendingSubtaskChoiceForTaskId
        if (pendingChoiceTaskId != null) {
            SubtaskChoiceChips(
                onCompleteAll = { onIntent(TaskListIntent.ResolveSubtaskChoice(pendingChoiceTaskId, true)) },
                onKeepOpen = { onIntent(TaskListIntent.ResolveSubtaskChoice(pendingChoiceTaskId, false)) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(OmniTheme.spacing.spazio3),
            )
        }
    }
}

@Composable
private fun TaskListContent(
    state: TaskListUiState,
    onIntent: (TaskListIntent) -> Unit,
    onTaskClick: (EntityId) -> Unit,
    onCapture: () -> Unit,
) {
    when {
        state.isLoading -> OmniLoadingState(modifier = Modifier.fillMaxSize())

        state.isEmpty ->
            OmniEmptyState(
                icon = OmniIconType.INFO,
                message = if (state.isFiltered) "Nessuna attività corrisponde" else "Nessuna attività qui",
                actionLabel = "Nuova attività",
                onActionClick = onCapture,
                modifier = Modifier.fillMaxSize(),
            )

        else ->
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                state.tasks.forEach { task ->
                    TaskRow(
                        task = task,
                        overdue = task.isOverdue(today),
                        onClick = { onTaskClick(task.envelope.id) },
                        onCompletedChange = { completed ->
                            val id = task.envelope.id
                            val intent = if (completed) TaskListIntent.Complete(id) else TaskListIntent.Uncomplete(id)
                            onIntent(intent)
                        },
                    )
                }
            }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    overdue: Boolean,
    onClick: () -> Unit,
    onCompletedChange: (Boolean) -> Unit,
) {
    OmniListItem(
        title = task.title,
        secondaryText = task.dueDate?.toString(),
        completed = task.completed,
        onCompletedChange = onCompletedChange,
        overdue = overdue,
        onClick = onClick,
    )
}

/** TASK-AC-03: an open-subtasks choice, never silently applied. */
@Composable
private fun SubtaskChoiceChips(
    onCompleteAll: () -> Unit,
    onKeepOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1),
    ) {
        OmniChip(text = "Completa tutti", selected = false, onClick = onCompleteAll)
        OmniChip(text = "Lascia aperti", selected = false, onClick = onKeepOpen)
    }
}
