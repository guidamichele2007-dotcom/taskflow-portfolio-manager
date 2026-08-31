@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.omnilife.feature.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.omnilife.core.common.EntityId
import com.omnilife.core.designsystem.components.OmniBottomSheet
import com.omnilife.core.designsystem.components.OmniButton
import com.omnilife.core.designsystem.components.OmniButtonVariant
import com.omnilife.core.designsystem.components.OmniCheckbox
import com.omnilife.core.designsystem.components.OmniDialog
import com.omnilife.core.designsystem.components.OmniSegmentedControl
import com.omnilife.core.designsystem.components.OmniTextField
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.domain.task.Subtask
import com.omnilife.domain.task.TaskPriority

private val PRIORITIES = TaskPriority.entries
private val PRIORITY_LABELS = listOf("Nessuna", "Media", "Alta")

/**
 * IA-035 (Task Detail) as the mandated Bottom Sheet (Navigation Bible §5: entity detail/edit is a
 * sheet, never a pushed screen). Every field autosaves (MFC-R-06) — no "save" button anywhere in
 * this tree.
 */
@Composable
public fun TaskDetailBottomSheet(
    state: TaskDetailUiState,
    onIntent: (TaskDetailIntent) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    // MUC §5: gentle auto-close when the entity was deleted elsewhere while this sheet was open.
    // A LaunchedEffect, not a direct call in the composition body, so this side effect runs once
    // per becoming true rather than on every recomposition.
    LaunchedEffect(state.noLongerAvailable) {
        if (state.noLongerAvailable) onDismiss()
    }
    if (state.noLongerAvailable) return

    OmniBottomSheet(onDismissRequest = onDismiss, modifier = modifier, sheetState = sheetState) {
        val task = state.task ?: return@OmniBottomSheet
        var showDeleteConfirm by remember { mutableStateOf(false) }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(OmniTheme.spacing.spazio3),
            verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2),
        ) {
            OmniTextField(
                value = task.title,
                onValueChange = { onIntent(TaskDetailIntent.ChangeTitle(it)) },
                label = "Titolo",
            )
            OmniSegmentedControl(
                segments = PRIORITY_LABELS,
                selectedIndex = PRIORITIES.indexOf(task.priority),
                onSegmentSelected = { onIntent(TaskDetailIntent.ChangePriority(PRIORITIES[it])) },
            )
            OmniTextField(
                value = task.notes.orEmpty(),
                onValueChange = { onIntent(TaskDetailIntent.ChangeNotes(it.ifBlank { null })) },
                label = "Note",
                singleLine = false,
            )
            SubtaskSection(
                subtasks = state.subtasks,
                newSubtaskTitle = state.newSubtaskTitle,
                onNewSubtaskTitleChange = { onIntent(TaskDetailIntent.ChangeNewSubtaskTitle(it)) },
                onAddSubtask = { onIntent(TaskDetailIntent.AddSubtask) },
                onToggleSubtask = { onIntent(TaskDetailIntent.ToggleSubtask(it)) },
                onDeleteSubtask = { onIntent(TaskDetailIntent.DeleteSubtask(it)) },
            )
            if (state.errorMessage != null) {
                BasicText(
                    text = state.errorMessage,
                    style = OmniTheme.typography.didascalia.copy(color = OmniTheme.colors.statoAttenzione),
                )
            }
            OmniButton(
                text = "Elimina",
                onClick = { showDeleteConfirm = true },
                variant = OmniButtonVariant.SECONDARIO,
            )
        }

        if (showDeleteConfirm) {
            OmniDialog(
                title = "Eliminare l'attività?",
                message = "Puoi ripristinarla dal cestino (MFC-R-09/R-10).",
                onDismissRequest = { showDeleteConfirm = false },
                confirmLabel = "Elimina",
                onConfirm = {
                    showDeleteConfirm = false
                    onIntent(TaskDetailIntent.Delete)
                },
                dismissLabel = "Annulla",
                onDismissClick = { showDeleteConfirm = false },
                isDestructiveConfirm = true,
            )
        }
    }
}

@Composable
private fun SubtaskSection(
    subtasks: List<Subtask>,
    newSubtaskTitle: String,
    onNewSubtaskTitleChange: (String) -> Unit,
    onAddSubtask: () -> Unit,
    onToggleSubtask: (EntityId) -> Unit,
    onDeleteSubtask: (EntityId) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1)) {
        subtasks.forEach { subtask ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1),
            ) {
                OmniCheckbox(
                    checked = subtask.completed,
                    onCheckedChange = { onToggleSubtask(subtask.id) },
                    contentDescription = subtask.title,
                )
                BasicText(
                    text = subtask.title,
                    modifier = Modifier.weight(1f),
                    style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoPrimario),
                )
                OmniButton(
                    text = null,
                    icon = OmniIconType.CLOSE,
                    iconContentDescription = "Rimuovi sottotask",
                    onClick = { onDeleteSubtask(subtask.id) },
                    variant = OmniButtonVariant.TESTUALE,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio1)) {
            OmniTextField(
                value = newSubtaskTitle,
                onValueChange = onNewSubtaskTitleChange,
                placeholder = "Nuovo sottotask",
                modifier = Modifier.weight(1f),
            )
            OmniButton(text = "Aggiungi", onClick = onAddSubtask, variant = OmniButtonVariant.SECONDARIO)
        }
    }
}
