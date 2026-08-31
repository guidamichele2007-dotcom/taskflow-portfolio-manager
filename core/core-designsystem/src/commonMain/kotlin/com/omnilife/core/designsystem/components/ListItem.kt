@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniOpacity

/**
 * CMP-RIGA-ENTITA (Design System Bible §06 "Riga Entità") — the single
 * anatomy shared by every entity type (Task, Transazione, Abitudine, Nota,
 * Obiettivo, P33): [completed]/[onCompletedChange] present only for
 * completable entities (`null` for Transazione/Nota), [trailingChip] for a
 * domain-specific status chip (the one "consentito" extension the Bible
 * allows, never a new anatomical element).
 *
 * Swipe-to-reveal quick actions and the long-press context menu are **not
 * implemented in this sprint** — [onLongClick] is exposed so a future menu
 * can hang off it, but no menu/swipe gesture exists yet (see Sprint 2
 * report, "componenti mancanti").
 */
@Composable
public fun OmniListItem(
    title: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    accessibilityLabel: String? = null,
    completed: Boolean? = null,
    onCompletedChange: ((Boolean) -> Unit)? = null,
    selected: Boolean = false,
    overdue: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    trailingChip: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isCompleted = completed == true
    val contentAlpha = if (isCompleted) OmniOpacity.COMPLETATO else 1f
    val rowShape = RoundedCornerShape(0)
    val background = if (selected) OmniTheme.colors.accento.copy(alpha = 0.12f) else Color.Transparent
    val description =
        accessibilityLabel ?: buildString {
            append(title)
            if (secondaryText != null) append(", $secondaryText")
            if (overdue) append(", in ritardo")
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OmniTheme.spacing.touchTargetMinimo)
                .background(background)
                .omniFocusRing(interactionSource, rowShape)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = { onClick?.invoke() },
                    onLongClick = onLongClick,
                ).padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1)
                .semantics(mergeDescendants = true) {
                    this.selected = selected
                    contentDescription = description
                },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (completed != null && onCompletedChange != null) {
            OmniCompletionControl(
                completed = completed,
                onToggle = { onCompletedChange(!completed) },
                entityLabel = title,
            )
            Spacer(Modifier.size(OmniTheme.spacing.spazio1))
        }
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = title,
                style =
                    OmniTheme.typography.corpoEnfatizzato.copy(
                        color = OmniTheme.colors.testoPrimario.copy(alpha = contentAlpha),
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    ),
            )
            if (secondaryText != null) {
                BasicText(
                    text = secondaryText,
                    style =
                        OmniTheme.typography.didascalia.copy(
                            color = if (overdue) OmniTheme.colors.statoAttenzione else OmniTheme.colors.testoSecondario,
                        ),
                )
            }
        }
        if (trailingChip != null) {
            Spacer(Modifier.size(OmniTheme.spacing.spazio1))
            trailingChip()
        }
    }
}
