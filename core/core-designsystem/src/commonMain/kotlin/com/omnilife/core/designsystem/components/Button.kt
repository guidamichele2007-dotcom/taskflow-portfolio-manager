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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniOpacity

/** CMP-PULSANTE weights (Design System Bible §06): primary (filled, accent), secondary (outline), text-only. */
public enum class OmniButtonVariant {
    PRIMARIO,
    SECONDARIO,
    TESTUALE,
}

/**
 * CMP-PULSANTE (Design System Bible §06 "Pulsante"). At least one of [text]/
 * [icon] must be non-null (an icon-only button still requires
 * [iconContentDescription], DS-07). Touch target enforced at
 * [com.omnilife.core.designtokens.OmniTouchTarget] regardless of visual
 * size (DS-06).
 *
 * States: `default`/`premuto` (handled by [omniPressScale] + [ripple]),
 * `disabilitato` ([enabled] = false, [OmniOpacity.DISABILITATO] +
 * interaction removed, never visual-only), `in_caricamento` ([loading] —
 * MFC §3's rare online exceptions only).
 */
@Composable
public fun OmniButton(
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OmniButtonVariant = OmniButtonVariant.PRIMARIO,
    icon: OmniIconType? = null,
    iconContentDescription: String? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    require(text != null || icon != null) { "OmniButton needs at least one of text/icon (CMP-PULSANTE anatomy)" }
    require(text != null || iconContentDescription != null) {
        "An icon-only OmniButton needs iconContentDescription (DS-07)"
    }

    val interactionSource = remember { MutableInteractionSource() }
    val shape = OmniTheme.shapes.medio
    val isInteractive = enabled && !loading

    val (background, contentColor, borderColor) = omniButtonColors(variant, enabled)

    Row(
        modifier =
            modifier
                .defaultMinSize(
                    minWidth = OmniTheme.spacing.touchTargetMinimo,
                    minHeight = OmniTheme.spacing.touchTargetMinimo,
                )
                .omniPressScale(interactionSource)
                .let { if (borderColor != null) it.border(1.dp, borderColor, shape) else it }
                .background(background, shape)
                .omniFocusRing(interactionSource, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    enabled = isInteractive,
                    role = Role.Button,
                    onClick = onClick,
                ).semantics { if (!enabled) disabled() }
                .padding(horizontal = OmniTheme.spacing.spazio3, vertical = OmniTheme.spacing.spazio2),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            OmniProgressDot(color = contentColor)
        } else {
            if (icon != null) {
                OmniIcon(
                    type = icon,
                    contentDescription = if (text == null) iconContentDescription else null,
                    tint = contentColor,
                )
                if (text != null) {
                    Spacer(Modifier.size(OmniTheme.spacing.spazio1))
                }
            }
            if (text != null) {
                BasicText(text = text, style = OmniTheme.typography.corpoEnfatizzato.copy(color = contentColor))
            }
        }
    }
}

/**
 * A dedicated, label-less icon button (CMP-PULSANTE "solo icona" variant)
 * — an accessible label is mandatory, never optional.
 */
@Composable
public fun OmniIconButton(
    icon: OmniIconType,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = OmniTheme.shapes.pieno
    val contentColor =
        if (enabled) {
            OmniTheme.colors.testoPrimario
        } else {
            OmniTheme.colors.testoPrimario.copy(
                alpha = OmniOpacity.DISABILITATO,
            )
        }

    Row(
        modifier =
            modifier
                .size(OmniTheme.spacing.touchTargetMinimo)
                .omniPressScale(interactionSource)
                .omniFocusRing(interactionSource, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = false),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ).semantics { if (!enabled) disabled() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OmniIcon(type = icon, contentDescription = contentDescription, tint = contentColor)
    }
}

private data class OmniButtonColorSet(val background: Color, val content: Color, val border: Color?)

@Composable
private fun omniButtonColors(
    variant: OmniButtonVariant,
    enabled: Boolean,
): OmniButtonColorSet {
    val accent = OmniTheme.colors.accento
    val onAccent = OmniTheme.colors.testoSuAccento
    val alpha = if (enabled) 1f else OmniOpacity.DISABILITATO
    return when (variant) {
        OmniButtonVariant.PRIMARIO ->
            OmniButtonColorSet(accent.copy(alpha = alpha), onAccent.copy(alpha = alpha), null)

        OmniButtonVariant.SECONDARIO ->
            OmniButtonColorSet(Color.Transparent, accent.copy(alpha = alpha), accent.copy(alpha = alpha))

        OmniButtonVariant.TESTUALE ->
            OmniButtonColorSet(Color.Transparent, accent.copy(alpha = alpha), null)
    }
}
