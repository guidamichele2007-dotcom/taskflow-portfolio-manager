@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniShape

/**
 * CMP-SHEET (Design System Bible §06 "Foglio", Bottom Sheet). Uses
 * Material3's [ModalBottomSheet] purely as the drag-to-dismiss/focus-trap
 * *behavior engine* (TDR-22) — every visual token (container color, shape,
 * drag handle) is [OmniTheme], never `MaterialTheme`. At most one sheet
 * open at a time is the caller's responsibility (Bible: never stacked).
 */
@Composable
public fun OmniBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = OmniTheme.colors.superficieElevata,
        contentColor = OmniTheme.colors.testoPrimario,
        shape = RoundedCornerShape(topStart = OmniShape.RAGGIO_GRANDE_DP.dp, topEnd = OmniShape.RAGGIO_GRANDE_DP.dp),
        dragHandle = { OmniSheetDragHandle() },
        content = content,
    )
}

@Composable
private fun OmniSheetDragHandle() {
    Box(modifier = Modifier.padding(vertical = OmniTheme.spacing.spazio1), contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier
                    .size(width = OmniTheme.spacing.spazio4, height = OmniTheme.border.spessoreFocus)
                    .background(OmniTheme.colors.bordoDefault, OmniTheme.shapes.pieno),
        )
    }
}
