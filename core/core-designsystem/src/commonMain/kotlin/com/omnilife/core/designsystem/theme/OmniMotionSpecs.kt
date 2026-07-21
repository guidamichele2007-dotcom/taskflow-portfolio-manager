package com.omnilife.core.designsystem.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import com.omnilife.core.designtokens.OmniMotionDurationMs

/**
 * [AnimationSpec]s for the 4 UX Bible MUC §4 durations/curves, each with the
 * "riduci movimento" equivalent DS-23/DS-30 requires: an instant [snap],
 * never a skipped animation call that would leave the target value
 * unapplied.
 */
public object OmniMotionSpecs {
    public fun <T> micro(reduceMotion: Boolean): AnimationSpec<T> =
        if (reduceMotion) snap() else tween(durationMillis = OmniMotionDurationMs.MICRO, easing = LinearOutSlowInEasing)

    public fun <T> standard(reduceMotion: Boolean): AnimationSpec<T> =
        if (reduceMotion) snap() else spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow)

    public fun <T> enfasi(reduceMotion: Boolean): AnimationSpec<T> =
        if (reduceMotion) snap() else spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium)

    public fun <T> uscita(reduceMotion: Boolean): AnimationSpec<T> =
        if (reduceMotion) snap() else tween(durationMillis = OmniMotionDurationMs.USCITA, easing = FastOutLinearInEasing)

    /** motion.progressione (Design System Bible §03 §3): interpolated, never a jump straight to the final value. */
    public fun <T> progressione(reduceMotion: Boolean): AnimationSpec<T> =
        if (reduceMotion) snap() else tween(durationMillis = OmniMotionDurationMs.STANDARD, easing = LinearOutSlowInEasing)
}
