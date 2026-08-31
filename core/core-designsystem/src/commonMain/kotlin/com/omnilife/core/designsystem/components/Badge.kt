package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * CMP-BADGE (Design System Bible §06 "Badge") — the **only** badge this
 * product allows anywhere (CAPT §4: reserved to the Inbox pending-work
 * count, never a generic notification/engagement counter). `count <= 0`
 * renders nothing at all (never a visible "0").
 *
 * Deliberately has no `contentDescription` of its own
 * ([Modifier.clearAndSetSemantics]): the Bible requires the badge to be
 * announced as part of the *host icon's* label ("Moduli, 3 elementi in
 * attesa"), never as a standalone element — the caller composes that
 * combined label onto the icon/container this badge is placed on.
 */
@Composable
public fun OmniBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    if (count <= 0) return
    Box(
        modifier =
            modifier
                .defaultMinSize(minWidth = OmniTheme.spacing.spazio3, minHeight = OmniTheme.spacing.spazio3)
                .background(OmniTheme.colors.accento, OmniTheme.shapes.pieno)
                .padding(horizontal = OmniTheme.spacing.spazio05)
                .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = count.toString(),
            style = OmniTheme.typography.didascalia.copy(color = OmniTheme.colors.testoSuAccento),
        )
    }
}
