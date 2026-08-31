package com.omnilife.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * The 3 [OmniEmptyVariant]s the Bible allows, each with its own icon —
 * never sharing the same glyph across variants (DS-12: "mai la stessa
 * illustrazione tra le due" — extended here to the error variant too).
 */
public enum class OmniEmptyVariant {
    MAI_USATO,
    FILTRATO,
    ERRORE,
}

/**
 * CMP-EMPTY (Design System Bible §06 "Blocco di Stato Vuoto"): fixed
 * 3-element anatomy — icon + benefit-oriented phrase + one primary action
 * — never a blank screen (DS "vietato"). [icon] is decorative (ignored by
 * screen readers); [message] and the action are always announced.
 */
@Composable
public fun OmniEmptyState(
    icon: OmniIconType,
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OmniEmptyVariant = OmniEmptyVariant.MAI_USATO,
) {
    val iconTint =
        if (variant == OmniEmptyVariant.ERRORE) OmniTheme.colors.statoCritico else OmniTheme.colors.testoSecondario
    Column(
        modifier = modifier.fillMaxWidth().padding(OmniTheme.spacing.spazio4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OmniTheme.spacing.spazio2),
    ) {
        Box(modifier = Modifier.size(OmniTheme.spacing.spazio8), contentAlignment = Alignment.Center) {
            OmniIcon(
                type = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(OmniTheme.spacing.spazio6),
            )
        }
        BasicText(
            text = message,
            style =
                TextStyle(
                    fontSize = OmniTheme.typography.corpoDefault.fontSize,
                    lineHeight = OmniTheme.typography.corpoDefault.lineHeight,
                    color = OmniTheme.colors.testoPrimario,
                    textAlign = TextAlign.Center,
                ),
        )
        OmniButton(text = actionLabel, onClick = onActionClick, variant = OmniButtonVariant.PRIMARIO)
    }
}

/**
 * The CMP-EMPTY "Errore" variant (Design System Bible §06: "rimando a
 * [10-error-experience]"), as its own named entry point per the Sprint 2
 * task list — not a new component, [variant] is simply pinned to
 * [OmniEmptyVariant.ERRORE].
 */
@Composable
public fun OmniErrorState(
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OmniEmptyState(
        icon = OmniIconType.ERROR,
        message = message,
        actionLabel = actionLabel,
        onActionClick = onActionClick,
        modifier = modifier,
        variant = OmniEmptyVariant.ERRORE,
    )
}
