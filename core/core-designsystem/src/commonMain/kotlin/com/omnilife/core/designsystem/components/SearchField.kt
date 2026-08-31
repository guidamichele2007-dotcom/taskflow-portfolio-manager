package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.omnilife.core.designsystem.theme.OmniIcon
import com.omnilife.core.designsystem.theme.OmniIconType
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * CMP-SEARCH (Design System Bible §06 "Campo di Ricerca"): incremental
 * results, no submit button (SRCH-001). [resultCount], when non-null, is
 * announced through a non-invasive live region (Bible: "annuncio del
 * numero di risultati... non invasivo") rather than interrupting focus —
 * pass `null` while the query is empty (no result count to announce yet).
 */
@Composable
public fun OmniSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cerca",
    resultCount: Int? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = OmniTheme.shapes.pieno

    Column(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OmniTheme.spacing.touchTargetMinimo)
                    .background(OmniTheme.colors.superficieElevata, shape)
                    .omniFocusRing(interactionSource, shape)
                    .padding(horizontal = OmniTheme.spacing.spazio2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OmniIcon(type = OmniIconType.SEARCH, contentDescription = null, tint = OmniTheme.colors.testoSecondario)
            Spacer(Modifier.size(OmniTheme.spacing.spazio1))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoPrimario),
                cursorBrush = SolidColor(OmniTheme.colors.accento),
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            BasicText(
                                text = placeholder,
                                style =
                                    OmniTheme.typography.corpoDefault.copy(
                                        color = OmniTheme.colors.testoSecondario,
                                    ),
                            )
                        }
                        innerTextField()
                    }
                },
            )
            if (query.isNotEmpty()) {
                OmniIconButton(
                    icon = OmniIconType.CLOSE,
                    contentDescription = "Cancella ricerca",
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(OmniTheme.spacing.spazio4),
                )
            }
        }
        if (resultCount != null) {
            Box(
                modifier =
                    Modifier.size(0.dp).semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = "$resultCount risultati trovati"
                    },
            )
        }
    }
}
