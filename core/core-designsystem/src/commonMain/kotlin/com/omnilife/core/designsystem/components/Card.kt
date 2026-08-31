package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designsystem.theme.omniElevation
import com.omnilife.core.designtokens.OmniElevationLevel

/**
 * CMP-CARD (Design System Bible §06 "Card"). [content] is the up-to-5-row
 * body (HOME-001 caps the count; enforcing the cap is the caller's
 * responsibility, this component only lays the slot out). Always
 * `elevazione.1` (the Bible fixes this, no variant raises it). Pass
 * [onSeeAllClick] to render the trailing "vedi tutto" action, always last.
 */
@Composable
public fun OmniCard(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
    seeAllLabel: String = "Vedi tutto",
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = OmniTheme.shapes.medio
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .omniElevation(OmniElevationLevel.LIVELLO_1, shape)
                .background(OmniTheme.colors.superficieElevata, shape)
                .padding(OmniTheme.spacing.spazio3),
    ) {
        androidx.compose.foundation.text.BasicText(
            text = title,
            style = OmniTheme.typography.titoloSezione.copy(color = OmniTheme.colors.testoPrimario),
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        content()
        if (onSeeAllClick != null) {
            Spacer(Modifier.height(OmniTheme.spacing.spazio2))
            Row(modifier = Modifier.fillMaxWidth()) {
                OmniButton(
                    text = seeAllLabel,
                    onClick = onSeeAllClick,
                    variant = OmniButtonVariant.TESTUALE,
                    icon = OmniIconType.CHEVRON_FORWARD,
                )
            }
        }
    }
}
