@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.omnilife.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import com.omnilife.core.designsystem.theme.OmniTheme

/**
 * CMP-SKELETON (Design System Bible §06 "Skeleton Loader") — a shape that
 * mirrors the real UI in arrival, never a generic block. Both variants are
 * `Modifier.semantics { invisibleToUser() }`-hidden: the Bible requires the
 * loading state to be announced once by the container
 * ([OmniLoadingState]), never read shape-by-shape.
 */
@Composable
public fun OmniSkeletonListItem(modifier: Modifier = Modifier) {
    val brush = omniShimmerBrush()
    Row(
        modifier =
            modifier.fillMaxWidth().height(
                OmniTheme.spacing.touchTargetMinimo,
            ).padding(horizontal = OmniTheme.spacing.spazio2)
                .semantics { invisibleToUser() },
    ) {
        Spacer(
            Modifier.size(OmniTheme.spacing.spazio4).background(brush, CircleShape),
        )
        Spacer(Modifier.width(OmniTheme.spacing.spazio2))
        Column {
            Spacer(
                Modifier.height(
                    OmniTheme.spacing.spazio2,
                ).width(OmniTheme.spacing.spazio8).background(brush, OmniTheme.shapes.piccolo),
            )
            Spacer(Modifier.height(OmniTheme.spacing.spazio05))
            Spacer(
                Modifier.height(
                    OmniTheme.spacing.spazio1,
                ).width(OmniTheme.spacing.spazio6).background(brush, OmniTheme.shapes.piccolo),
            )
        }
    }
}

@Composable
public fun OmniSkeletonCard(modifier: Modifier = Modifier) {
    val brush = omniShimmerBrush()
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(OmniTheme.colors.superficieElevata, OmniTheme.shapes.medio)
                .padding(OmniTheme.spacing.spazio3)
                .semantics { invisibleToUser() },
    ) {
        Spacer(
            Modifier.height(
                OmniTheme.spacing.spazio3,
            ).width(OmniTheme.spacing.spazio8).background(brush, OmniTheme.shapes.piccolo),
        )
        Spacer(Modifier.height(OmniTheme.spacing.spazio2))
        repeat(3) {
            Spacer(
                Modifier.height(OmniTheme.spacing.spazio3).fillMaxWidth().background(brush, OmniTheme.shapes.piccolo),
            )
            Spacer(Modifier.height(OmniTheme.spacing.spazio1))
        }
    }
}
