@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.omnilife.feature.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omnilife.core.designsystem.components.OmniBottomSheet
import com.omnilife.core.designsystem.components.OmniButton
import com.omnilife.core.designsystem.components.OmniSegmentedControl
import com.omnilife.core.designsystem.components.OmniTextField
import com.omnilife.core.designsystem.components.OmniToggle
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.domain.task.TaskPriority

private val PRIORITIES = TaskPriority.entries
private val PRIORITY_LABELS = listOf("Nessuna", "Media", "Alta")

/**
 * Quick Capture / create-task (Macro Sprint 5's scoped-down slice of the future CAPT system —
 * see `TaskCreateViewModel`'s doc). A Bottom Sheet, same anatomy convention as [TaskDetailBottomSheet]
 * (Navigation Bible §5).
 */
@Composable
public fun TaskCreateBottomSheet(
    state: TaskCreateUiState,
    onIntent: (TaskCreateIntent) -> Unit,
    onDismiss: () -> Unit,
    onCreated: (com.omnilife.core.common.EntityId) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    LaunchedEffect(state.createdTaskId) {
        val createdId = state.createdTaskId
        if (createdId != null) {
            onCreated(createdId)
            onIntent(TaskCreateIntent.ConsumeCreated)
        }
    }

    OmniBottomSheet(onDismissRequest = onDismiss, modifier = modifier, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(OmniTheme.spacing.spazio3),
            verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2),
        ) {
            OmniTextField(
                value = state.title,
                onValueChange = { onIntent(TaskCreateIntent.ChangeTitle(it)) },
                label = "Titolo",
                placeholder = "Cosa devi fare?",
                isError = state.errorMessage != null,
                errorMessage = state.errorMessage,
            )
            OmniSegmentedControl(
                segments = PRIORITY_LABELS,
                selectedIndex = PRIORITIES.indexOf(state.priority),
                onSegmentSelected = { onIntent(TaskCreateIntent.ChangePriority(PRIORITIES[it])) },
            )
            if (state.dueDate != null && state.dueTime != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        text = "Promemoria",
                        style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoPrimario),
                    )
                    OmniToggle(
                        checked = state.reminderEnabled,
                        onCheckedChange = { onIntent(TaskCreateIntent.ToggleReminder(it)) },
                    )
                }
            }
            OmniButton(
                text = "Crea attività",
                onClick = { onIntent(TaskCreateIntent.Save) },
                enabled = state.canSave,
                loading = state.isSaving,
            )
        }
    }
}
