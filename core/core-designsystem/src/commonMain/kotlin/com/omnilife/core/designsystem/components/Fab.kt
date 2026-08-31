@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designsystem.theme.omniElevation
import com.omnilife.core.designtokens.OmniElevationLevel

/**
 * CMP-FAB (Design System Bible §06 "Pulsante di Cattura"): one per screen,
 * always the same meaning (capture), never repurposed. [onLongClick] hangs
 * the radial shortcut menu (CAPT-007) — the menu itself is not implemented
 * in this sprint (see Sprint 2 report, "componenti mancanti").
 */
@Composable
public fun OmniFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    contentDescription: String = "Cattura rapida",
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = OmniTheme.shapes.pieno
    Box(
        modifier =
            modifier
                .size(OmniTheme.spacing.spazio6)
                .omniPressScale(interactionSource)
                .omniElevation(OmniElevationLevel.LIVELLO_2, shape)
                .background(OmniTheme.colors.accento, shape)
                .omniFocusRing(interactionSource, shape)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick,
                    onLongClick = onLongClick,
                    role = Role.Button,
                ).semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        OmniIcon(type = OmniIconType.ADD, contentDescription = null, tint = OmniTheme.colors.testoSuAccento)
    }
}
