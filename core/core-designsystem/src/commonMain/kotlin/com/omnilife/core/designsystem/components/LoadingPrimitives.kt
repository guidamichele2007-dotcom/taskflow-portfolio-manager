package com.omnilife.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * `motion.shimmer` (Design System Bible §03 §3): the only loading animation
 * this library uses, never a spinner (DS-17 "in_caricamento" state). Loops
 * only while composed — the caller removes the modifier the instant real
 * content replaces the skeleton (DS-29: no perpetual loop past its state).
 */
@Composable
public fun omniShimmerBrush(): Brush {
    val base = OmniTheme.colors.bordoDefault
    val highlight = OmniTheme.colors.superficieElevata
    if (OmniTheme.reduceMotion) {
        return Brush.linearGradient(listOf(base, base))
    }
    val transition = rememberInfiniteTransition(label = "omniShimmer")
    val translate by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "omniShimmerTranslate",
        )
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(translate * 600f - 300f, 0f),
        end = Offset(translate * 600f, 300f),
    )
}

/**
 * Small inline loading indicator for `in_caricamento` buttons (MFC §3's
 * rare online exceptions) — never a full-screen spinner.
 */
@Composable
public fun OmniProgressDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "omniProgressDot")
    val alpha by
        transition.animateFloat(
            initialValue = if (OmniTheme.reduceMotion) 1f else 0.3f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
            label = "omniProgressDotAlpha",
        )
    Box(modifier = modifier.size(8.dp).background(color.copy(alpha = alpha), CircleShape))
}
