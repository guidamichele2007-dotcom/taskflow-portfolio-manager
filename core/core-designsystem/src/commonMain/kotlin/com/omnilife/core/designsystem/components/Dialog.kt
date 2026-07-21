package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designsystem.theme.omniElevation
import com.omnilife.core.designtokens.OmniElevationLevel

/**
 * CMP-DIALOG (Design System Bible §06 "Dialogo") — reserved exclusively
 * for irreversible actions (UX-R-008), never a generic "are you sure?".
 * Uses [androidx.compose.ui.window.Dialog] purely for its platform focus
 * trap/backdrop behavior; every visual token is [OmniTheme].
 *
 * The Bible's rule "the primary action is never the destructive one" is
 * enforced here directly: when [isDestructiveConfirm] is true,
 * [confirmLabel] renders with the *lesser* visual weight (outline) and
 * [dismissLabel] gets the prominent, filled one — a distracted tap
 * defaults to safety, never to destruction.
 */
@Composable
public fun OmniDialog(
    title: String,
    onDismissRequest: () -> Unit,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    dismissLabel: String? = null,
    onDismissClick: (() -> Unit)? = null,
    isDestructiveConfirm: Boolean = false,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        val shape = OmniTheme.shapes.medio
        Column(
            modifier =
                modifier
                    .widthIn(max = 400.dp)
                    .omniElevation(OmniElevationLevel.LIVELLO_3, shape)
                    .background(OmniTheme.colors.superficieElevata, shape)
                    .padding(OmniTheme.spacing.spazio3),
        ) {
            BasicText(
                text = title,
                style = OmniTheme.typography.titoloSezione.copy(color = OmniTheme.colors.testoPrimario),
                modifier = Modifier.semantics { heading() },
            )
            if (message != null) {
                Spacer(Modifier.height(OmniTheme.spacing.spazio2))
                BasicText(
                    text = message,
                    style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoSecondario),
                )
            }
            Spacer(Modifier.height(OmniTheme.spacing.spazio3))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val safeVariant = OmniButtonVariant.PRIMARIO
                val destructiveVariant = OmniButtonVariant.SECONDARIO
                if (dismissLabel != null && onDismissClick != null) {
                    OmniButton(
                        text = dismissLabel,
                        onClick = onDismissClick,
                        variant = if (isDestructiveConfirm) safeVariant else OmniButtonVariant.TESTUALE,
                    )
                    Spacer(Modifier.width(OmniTheme.spacing.spazio2))
                }
                OmniButton(
                    text = confirmLabel,
                    onClick = onConfirm,
                    variant = if (isDestructiveConfirm) destructiveVariant else safeVariant,
                )
            }
        }
    }
}
