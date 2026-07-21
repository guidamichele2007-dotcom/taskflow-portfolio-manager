package com.omnilife.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.omnilife.core.designsystem.theme.OmniMotionSpecs
import com.omnilife.core.designsystem.theme.OmniTheme
import kotlinx.coroutines.delay

/**
 * One CMP-SNACKBAR message. [onSupersededSilently] fires if a second
 * message replaces this one before its own timeout (MUC §8).
 */
public data class OmniSnackbarMessage(
    val text: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val onSupersededSilently: (() -> Unit)? = null,
)

/** Holds at most one visible [OmniSnackbarMessage] — a second `show()` silently supersedes the first (Bible MUC §8). */
public class OmniSnackbarHostState {
    public var current: OmniSnackbarMessage? by mutableStateOf(null)
        private set

    public fun show(message: OmniSnackbarMessage) {
        current?.onSupersededSilently?.invoke()
        current = message
    }

    internal fun dismiss() {
        current = null
    }
}

@Composable
public fun rememberOmniSnackbarHostState(): OmniSnackbarHostState = remember { OmniSnackbarHostState() }

private const val SNACKBAR_DURATION_MS = 7000L

/**
 * CMP-SNACKBAR (Design System Bible §06 "Snackbar"): fixed 7s duration
 * (MUC §2), at most one visible at a time. Announced as a non-invasive
 * live region, never interrupting the current focus.
 */
@Composable
public fun OmniSnackbarHost(
    state: OmniSnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val message = state.current
    LaunchedEffect(message) {
        if (message != null) {
            delay(SNACKBAR_DURATION_MS)
            state.dismiss()
        }
    }
    val reduceMotion = OmniTheme.reduceMotion
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(OmniMotionSpecs.standard(reduceMotion)) { it },
        exit = slideOutVertically(OmniMotionSpecs.uscita(reduceMotion)) { it },
        modifier = modifier,
    ) {
        if (message != null) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(OmniTheme.colors.superficieElevata, OmniTheme.shapes.medio)
                        .padding(horizontal = OmniTheme.spacing.spazio2, vertical = OmniTheme.spacing.spazio1)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = message.text,
                    style = OmniTheme.typography.corpoDefault.copy(color = OmniTheme.colors.testoPrimario),
                )
                if (message.actionLabel != null) {
                    OmniButton(
                        text = message.actionLabel,
                        onClick = {
                            message.onAction?.invoke()
                            state.dismiss()
                        },
                        variant = OmniButtonVariant.TESTUALE,
                    )
                }
            }
        }
    }
}
