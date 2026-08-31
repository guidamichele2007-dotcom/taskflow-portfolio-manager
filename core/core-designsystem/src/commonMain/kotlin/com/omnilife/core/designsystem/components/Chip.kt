package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniOpacity

/** CMP-CHIP variants (Design System Bible §06): single-select, removable filter, and recent-value suggestion. */
public enum class OmniChipVariant {
    SELEZIONE,
    FILTRO,
    SUGGERIMENTO,
}

/**
 * CMP-CHIP (Design System Bible §06 "Chip"). [onRemove] is required for
 * [OmniChipVariant.FILTRO] (the only variant the Bible allows a removal
 * action on) and ignored otherwise. Touch target: the whole chip row is at
 * least [com.omnilife.core.designtokens.OmniTouchTarget] tall (DS-34 makes
 * no size exception for small components).
 */
@Composable
public fun OmniChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OmniChipVariant = OmniChipVariant.SELEZIONE,
    icon: OmniIconType? = null,
    enabled: Boolean = true,
    onRemove: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = OmniTheme.shapes.pieno
    val accent = OmniTheme.colors.accento
    val alpha = if (enabled) 1f else OmniOpacity.DISABILITATO
    val background =
        if (selected) {
            accent.copy(
                alpha = alpha,
            )
        } else {
            OmniTheme.colors.superficieElevata.copy(alpha = alpha)
        }
    val contentColor =
        if (selected) {
            OmniTheme.colors.testoSuAccento
        } else {
            OmniTheme.colors.testoPrimario.copy(
                alpha = alpha,
            )
        }
    val borderColor = OmniTheme.colors.bordoDefault.copy(alpha = alpha)

    Row(
        modifier =
            modifier
                .defaultMinSize(minHeight = OmniTheme.spacing.touchTargetMinimo)
                .omniPressScale(interactionSource)
                .background(background, shape)
                .border(OmniTheme.border.spessoreDefault, borderColor, shape)
                .omniFocusRing(interactionSource, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ).semantics {
                    this.selected = selected
                    if (!enabled) disabled()
                }.padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio05),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            OmniIcon(type = icon, contentDescription = null, tint = contentColor)
            Spacer(Modifier.size(OmniTheme.spacing.spazio05))
        }
        BasicText(text = text, style = OmniTheme.typography.etichetta.copy(color = contentColor))
        if (variant == OmniChipVariant.FILTRO && onRemove != null) {
            Spacer(Modifier.size(OmniTheme.spacing.spazio1))
            OmniIconButton(
                icon = OmniIconType.CLOSE,
                contentDescription = "Rimuovi filtro $text",
                onClick = onRemove,
                modifier = Modifier.size(OmniTheme.spacing.spazio4).defaultMinSize(minWidth = 0.dp, minHeight = 0.dp),
                enabled = enabled,
            )
        }
    }
}
