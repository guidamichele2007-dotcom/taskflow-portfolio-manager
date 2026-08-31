package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * CMP-SEGMENT (Design System Bible §06 "Controllo Segmentato"): 2-4
 * same-level view segments, never a destructive choice (navigation only).
 */
@Composable
public fun OmniSegmentedControl(
    segments: List<String>,
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(segments.size in 2..4) { "CMP-SEGMENT: 2-4 segments only (P28)" }
    val shape = OmniTheme.shapes.medio
    Row(
        modifier =
            modifier
                .background(OmniTheme.colors.superficieBase, shape)
                .padding(OmniTheme.spacing.spazio05)
                .selectableGroup(),
    ) {
        segments.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = OmniTheme.spacing.touchTargetMinimo)
                        .background(if (selected) OmniTheme.colors.accento else Color.Transparent, shape)
                        .selectable(selected = selected, onClick = { onSegmentSelected(index) }, role = Role.Tab),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = label,
                    style =
                        OmniTheme.typography.etichetta.copy(
                            color = if (selected) OmniTheme.colors.testoSuAccento else OmniTheme.colors.testoPrimario,
                        ),
                )
            }
        }
    }
}
