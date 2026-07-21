package com.omnilife.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.omnilife.core.designsystem.theme.OmniMotionSpecs
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniMotionScale

/**
 * `motion.scala.pressione` (Design System Bible §03 §3): every pressable
 * `Omni*` component scales down by the same few percent while pressed,
 * never a bespoke per-component press effect.
 */
@Composable
public fun Modifier.omniPressScale(interactionSource: MutableInteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val reduceMotion = OmniTheme.reduceMotion
    val scale by
        animateFloatAsState(
            targetValue = if (pressed) OmniMotionScale.PRESSIONE else 1f,
            animationSpec = OmniMotionSpecs.micro(reduceMotion),
            label = "omniPressScale",
        )
    return this.scale(scale)
}

/**
 * DS-32: an always-visible focus indicator when reached by
 * keyboard/pointer, using [OmniTheme.colors]' `bordoFocus` — independent of
 * the chosen accent (see TDR-22) so focus visibility never depends on
 * which accent the user picked.
 */
@Composable
public fun Modifier.omniFocusRing(
    interactionSource: MutableInteractionSource,
    shape: Shape = RoundedCornerShape(0),
): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    val color = if (focused) OmniTheme.colors.bordoFocus else Color.Transparent
    val width = if (focused) OmniTheme.border.spessoreFocus else 0.dp
    return this.border(width = width, color = color, shape = shape)
}
