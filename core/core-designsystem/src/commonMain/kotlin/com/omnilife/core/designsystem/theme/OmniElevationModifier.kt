package com.omnilife.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.omnilife.core.designtokens.OmniElevationLevel

/**
 * DS-04/DS-25: the shadow is only the secondary channel — the primary one
 * is the tonal contrast already built into `superficieElevata` vs.
 * `superficieBase` (see [OmniColorScheme]), which every elevated component
 * uses as its background regardless of this modifier.
 */
@Composable
public fun Modifier.omniElevation(
    level: OmniElevationLevel,
    shape: Shape,
): Modifier = this.shadow(elevation = level.shadowDp.dp, shape = shape, clip = false)
