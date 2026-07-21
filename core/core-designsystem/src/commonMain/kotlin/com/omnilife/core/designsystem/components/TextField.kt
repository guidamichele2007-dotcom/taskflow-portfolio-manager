package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * A generic single-line text input — not individually named in the Design
 * System Bible (only [OmniSearchField], CMP-SEARCH, is), but required by
 * every form the Functional Bible describes (task title/notes, list names,
 * ...). Extends the existing "campo di input" token already referenced in
 * [01-token-visivi §5](../../../../../../../../docs/omnilife/design_system_bible/01-token-visivi.md)
 * (`raggio.medio`) rather than inventing a new visual language (DS-INV-04)
 * — see the Sprint 2 report for this documented extension.
 *
 * States: `default`/`in_evidenza` (focus ring, DS-32), `disabilitato`,
 * `in_errore` (amber border + inline message, never `stato.critico` per
 * DS-16 in the Design System Bible's states/accessibility chapter).
 */
@Composable
public fun OmniTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val shape = OmniTheme.shapes.medio
    val borderColor =
        when {
            isError -> OmniTheme.colors.statoAttenzione
            focused -> OmniTheme.colors.bordoFocus
            else -> OmniTheme.colors.bordoDefault
        }
    val borderWidth = if (isError || focused) OmniTheme.border.spessoreFocus else OmniTheme.border.spessoreDefault

    Column(modifier = modifier) {
        if (label != null) {
            BasicText(
                text = label,
                style = OmniTheme.typography.etichetta.copy(color = OmniTheme.colors.testoSecondario),
            )
            Spacer(Modifier.height(OmniTheme.spacing.spazio05))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OmniTheme.spacing.touchTargetMinimo)
                    .border(borderWidth, borderColor, shape)
                    .background(OmniTheme.colors.superficieElevata, shape)
                    .padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1)
                    .semantics {
                        if (isError) error(errorMessage ?: "Valore non valido")
                        if (!enabled) disabled()
                    },
            enabled = enabled,
            singleLine = singleLine,
            textStyle = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoPrimario),
            cursorBrush = SolidColor(OmniTheme.colors.accento),
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty() && placeholder != null) {
                        BasicText(
                            text = placeholder,
                            style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoSecondario),
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(OmniTheme.spacing.spazio05))
            BasicText(
                text = errorMessage,
                style = OmniTheme.typography.didascalia.copy(color = OmniTheme.colors.statoAttenzione),
            )
        }
    }
}
