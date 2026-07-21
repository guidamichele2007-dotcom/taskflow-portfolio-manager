package com.omnilife.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniMotionDurationMs
import com.omnilife.core.designtokens.OmniOpacity

/**
 * CMP-COMPLETION (Design System Bible §06 "Controllo di Completamento"):
 * one tap, no confirmation dialog ever (P2 — undo comes after). The
 * checkmark fades/scales in with `motion.micro` — a substitute for a true
 * progressive stroke-draw animation (Bible: "stroke della spunta animato"),
 * documented as a simplification in the Sprint 2 report.
 *
 * `entityLabel` feeds the explicit state-change announcement the Bible
 * requires ("Completato: [titolo]").
 */
@Composable
public fun OmniCompletionControl(
    completed: Boolean,
    onToggle: () -> Unit,
    entityLabel: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OmniBinaryMark(
        checked = completed,
        onToggle = onToggle,
        modifier = modifier,
        enabled = enabled,
        shape = CircleShape,
        stateDescription = if (completed) "Completato: $entityLabel" else "Non completato: $entityLabel",
        role = Role.Checkbox,
    )
}

/**
 * A general-purpose binary checkbox for non-entity contexts (multi-select
 * lists, settings) — same visual language as [OmniCompletionControl] but
 * square (Bible: "coerenza per tipo, mai mista nello stesso contesto" —
 * circular is reserved for entity completion, square for everything else).
 */
@Composable
public fun OmniCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OmniBinaryMark(
        checked = checked,
        onToggle = { onCheckedChange(!checked) },
        modifier = modifier,
        enabled = enabled,
        shape = OmniTheme.shapes.piccolo,
        stateDescription = "$contentDescription, ${if (checked) "selezionato" else "non selezionato"}",
        role = Role.Checkbox,
    )
}

@Composable
private fun OmniBinaryMark(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    stateDescription: String,
    role: Role,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val alpha = if (enabled) 1f else OmniOpacity.DISABILITATO
    val accent = OmniTheme.colors.accento
    val border = OmniTheme.colors.bordoDefault

    Box(
        modifier =
            modifier
                .defaultMinSize(
                    minWidth = OmniTheme.spacing.touchTargetMinimo,
                    minHeight = OmniTheme.spacing.touchTargetMinimo,
                )
                .omniFocusRing(interactionSource, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = role,
                    onClick = onToggle,
                ).semantics {
                    this.stateDescription = stateDescription
                    if (!enabled) disabled()
                },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(OmniTheme.spacing.spazio3)
                    .background(
                        if (checked) {
                            accent.copy(
                                alpha = alpha,
                            )
                        } else {
                            OmniTheme.colors.superficieElevata.copy(alpha = alpha)
                        },
                        shape,
                    )
                    .border(OmniTheme.border.spessoreDefault, border.copy(alpha = alpha), shape),
            contentAlignment = Alignment.Center,
        ) {
            val micro = tween<Float>(OmniMotionDurationMs.MICRO)
            AnimatedVisibility(
                visible = checked,
                enter = fadeIn(micro) + scaleIn(micro, initialScale = 0.6f),
                exit = fadeOut(micro) + scaleOut(micro, targetScale = 0.6f),
            ) {
                OmniIcon(type = OmniIconType.CHECK, contentDescription = null, tint = OmniTheme.colors.testoSuAccento)
            }
        }
    }
}
