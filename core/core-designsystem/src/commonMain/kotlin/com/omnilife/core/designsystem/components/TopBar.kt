package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme

/** One CMP-TOPBAR contextual action — the Bible caps this list at 2 (P28). */
public data class OmniTopBarAction(
    val icon: OmniIconType,
    val contentDescription: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * CMP-TOPBAR (Design System Bible §06 "Top Bar"). [onBackClick] present
 * only on L2/detail views (the L1 variant omits it). The title is forced
 * to read first in the accessibility traversal order (`traversalIndex`)
 * regardless of the back arrow's visual position before it, per the
 * Bible's explicit requirement.
 */
@Composable
public fun OmniTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: List<OmniTopBarAction> = emptyList(),
) {
    require(actions.size <= 2) { "CMP-TOPBAR: max 2 contextual actions (P28)" }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OmniTheme.spacing.touchTargetMinimo)
                .background(OmniTheme.colors.superficieBase)
                .padding(horizontal = OmniTheme.spacing.spazio1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBackClick != null) {
            OmniIconButton(icon = OmniIconType.ARROW_BACK, contentDescription = "Indietro", onClick = onBackClick)
            Spacer(Modifier.size(OmniTheme.spacing.spazio1))
        }
        BasicText(
            text = title,
            style = OmniTheme.typography.titoloSchermata.copy(color = OmniTheme.colors.testoPrimario),
            modifier =
                Modifier.weight(1f).semantics {
                    heading()
                    traversalIndex = -1f
                },
        )
        actions.forEach { action ->
            OmniIconButton(
                icon = action.icon,
                contentDescription = action.contentDescription,
                onClick = action.onClick,
                enabled = action.enabled,
            )
        }
    }
}
