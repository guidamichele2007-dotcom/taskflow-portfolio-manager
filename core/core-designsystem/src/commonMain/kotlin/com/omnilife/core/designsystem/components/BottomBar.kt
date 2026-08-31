package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme

/** One CMP-TABBAR destination — icon differs between `default` and `selezionato` (never color alone, DS-08). */
public data class OmniTabBarItem(
    val icon: OmniIconType,
    val selectedIcon: OmniIconType,
    val label: String,
)

/**
 * CMP-TABBAR (Design System Bible §06 "Tab Bar") — the 4 first-level
 * destinations, always exactly 4 slots (never variable per module,
 * DS-INV-06).
 */
@Composable
public fun OmniBottomBar(
    items: List<OmniTabBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(items.size == 4) { "CMP-TABBAR: exactly 4 slots, never variable (UX Bible §1)" }
    Row(
        modifier =
            modifier.fillMaxWidth().background(
                OmniTheme.colors.superficieElevata,
            ).padding(vertical = OmniTheme.spacing.spazio1),
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            val tint = if (selected) OmniTheme.colors.accento else OmniTheme.colors.testoSecondario
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = OmniTheme.spacing.touchTargetMinimo)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Tab,
                            onClick = { onItemSelected(index) },
                        ).semantics { this.selected = selected },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                OmniIcon(type = if (selected) item.selectedIcon else item.icon, contentDescription = null, tint = tint)
                Spacer(Modifier.height(OmniTheme.spacing.spazio05))
                BasicText(text = item.label, style = OmniTheme.typography.didascalia.copy(color = tint))
            }
        }
    }
}
