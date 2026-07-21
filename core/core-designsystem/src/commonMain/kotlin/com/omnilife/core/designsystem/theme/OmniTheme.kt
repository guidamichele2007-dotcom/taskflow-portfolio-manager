package com.omnilife.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.omnilife.core.designtokens.OmniAccent

private val LocalOmniColorScheme =
    staticCompositionLocalOf<OmniColorScheme> {
        error("OmniTheme { ... } was not applied — every OmniDesignSystem composable must be inside one")
    }
private val LocalOmniTypographyStyles = staticCompositionLocalOf { buildOmniTypographyStyles() }
private val LocalOmniSpacingDp = staticCompositionLocalOf { buildOmniSpacingDp() }
private val LocalOmniShapes = staticCompositionLocalOf { buildOmniShapes() }
private val LocalOmniBorderDp = staticCompositionLocalOf { buildOmniBorderDp() }

/** DS-23: "riduci movimento" — every OmniTheme descendant reads this instead of querying platform APIs directly. */
private val LocalOmniReduceMotion = staticCompositionLocalOf { false }

/**
 * Single entry point for the whole component library (P55/UX-C-291: one
 * design system, no per-module exception). Every `Omni*` composable reads
 * its tokens from [OmniTheme], never from a platform theme
 * (`MaterialTheme`) directly.
 *
 * @param darkTheme which of the DS-INV-01 explicit pairs to resolve.
 * @param accent the one active [OmniAccent] (Functional Bible SET-001 §2) —
 *   a single value, never a per-module palette.
 * @param reduceMotion mirrors the platform's "reduce motion" system
 *   setting; the caller (the future app shell) is responsible for querying
 *   it per-platform and passing it in — no such shell exists yet in this
 *   sprint (no screens), so it defaults to `false`.
 */
@Composable
public fun OmniTheme(
    darkTheme: Boolean,
    accent: OmniAccent = OmniAccent.BLU,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(darkTheme, accent) { buildOmniColorScheme(darkTheme, accent) }
    CompositionLocalProvider(
        LocalOmniColorScheme provides colorScheme,
        LocalOmniReduceMotion provides reduceMotion,
        content = content,
    )
}

/** Read-only facade every `Omni*` component uses to reach the current tokens — never a raw `Local*.current`. */
public object OmniTheme {
    public val colors: OmniColorScheme
        @Composable get() = LocalOmniColorScheme.current

    public val typography: OmniTypographyStyles
        @Composable get() = LocalOmniTypographyStyles.current

    public val spacing: OmniSpacingDp
        @Composable get() = LocalOmniSpacingDp.current

    public val shapes: OmniShapes
        @Composable get() = LocalOmniShapes.current

    public val border: OmniBorderDp
        @Composable get() = LocalOmniBorderDp.current

    public val reduceMotion: Boolean
        @Composable get() = LocalOmniReduceMotion.current
}
