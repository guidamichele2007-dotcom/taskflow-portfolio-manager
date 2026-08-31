package com.omnilife.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.omnilife.core.designsystem.theme.OmniMotionSpecs
import com.omnilife.core.designsystem.theme.OmniTheme
import com.omnilife.core.designtokens.OmniOpacity
import androidx.compose.foundation.interaction.MutableInteractionSource as InteractionSource

/**
 * CMP-TOGGLE (Design System Bible §06 "Interruttore") — closed catalog of
 * boolean settings (SET-R-01): effect is immediate (SET-R-02, no
 * save/apply step). State is announced explicitly on/off (never inferred
 * from thumb position alone).
 */
@Composable
public fun OmniToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { InteractionSource() }
    val reduceMotion = OmniTheme.reduceMotion
    val trackWidth = OmniTheme.spacing.spazio4
    val trackHeight = OmniTheme.spacing.spazio2
    val thumbInset = 2.dp
    val thumbSize = trackHeight - thumbInset * 2

    val alpha = if (enabled) 1f else OmniOpacity.DISABILITATO
    val trackColor = if (checked) OmniTheme.colors.accento else OmniTheme.colors.bordoDefault
    val thumbOffset by
        animateDpAsState(
            targetValue = if (checked) trackWidth - thumbSize - thumbInset else thumbInset,
            animationSpec = OmniMotionSpecs.micro(reduceMotion),
            label = "omniToggleThumb",
        )

    Box(
        modifier =
            modifier
                .defaultMinSize(
                    minWidth = OmniTheme.spacing.touchTargetMinimo,
                    minHeight = OmniTheme.spacing.touchTargetMinimo,
                )
                .wrapContentSize(Alignment.Center),
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = trackWidth, height = trackHeight)
                    .background(trackColor.copy(alpha = alpha), CircleShape)
                    .omniFocusRing(interactionSource, CircleShape)
                    .toggleable(
                        value = checked,
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        role = Role.Switch,
                        onValueChange = onCheckedChange,
                    ).semantics { if (!enabled) disabled() },
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(start = thumbOffset)
                        .size(thumbSize)
                        .background(OmniTheme.colors.superficieElevata.copy(alpha = alpha), CircleShape),
            )
        }
    }
}
