package com.omnilife.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import com.omnilife.core.designsystem.theme.OmniMotionSpecs
import com.omnilife.core.designsystem.theme.OmniTheme

/** CMP-PROGRESS shapes (Design System Bible §06): ring for habits/goals, bar for budget. */
public enum class OmniProgressShape {
    ANELLO,
    BARRA,
}

/** The only 3 states the Bible allows (never a 4th "alarm" level, P48). */
public enum class OmniProgressStatus {
    DEFAULT,
    ATTENZIONE,
    COMPLETO,
}

/**
 * CMP-PROGRESS (Design System Bible §06 "Indicatore di Progresso"). [label]
 * is the textual value that must always accompany the graphic (DS-INV-05 —
 * never the graphic alone), e.g. "82% del budget Alimentari": it becomes
 * both the visible caption and the accessible value, read instead of the
 * raw fraction. The fill always animates via `motion.progressione`, never a
 * jump to the final value.
 */
@Composable
public fun OmniProgress(
    value: Float,
    label: String,
    modifier: Modifier = Modifier,
    shape: OmniProgressShape = OmniProgressShape.BARRA,
    status: OmniProgressStatus = OmniProgressStatus.DEFAULT,
) {
    val clamped = value.coerceIn(0f, 1f)
    val reduceMotion = OmniTheme.reduceMotion
    val animatedValue by
        animateFloatAsState(
            targetValue = clamped,
            animationSpec = OmniMotionSpecs.progressione(reduceMotion),
            label = "omniProgressValue",
        )
    val color =
        when (status) {
            OmniProgressStatus.DEFAULT -> OmniTheme.colors.accento
            OmniProgressStatus.ATTENZIONE -> OmniTheme.colors.statoAttenzione
            OmniProgressStatus.COMPLETO -> OmniTheme.colors.statoPositivoSobrio
        }
    val trackColor = OmniTheme.colors.bordoDefault

    Column(
        modifier =
            modifier.semantics {
                contentDescription = label
                progressBarRangeInfo = ProgressBarRangeInfo(current = clamped, range = 0f..1f)
            },
    ) {
        when (shape) {
            OmniProgressShape.BARRA ->
                Box(
                    Modifier.fillMaxWidth().height(
                        OmniTheme.spacing.spazio1,
                    ).background(trackColor, OmniTheme.shapes.medio),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(animatedValue)
                            .fillMaxHeight()
                            .background(color, OmniTheme.shapes.medio),
                    )
                }

            OmniProgressShape.ANELLO -> {
                val strokeWidth = OmniTheme.spacing.spazio05
                Canvas(modifier = Modifier.size(OmniTheme.spacing.spazio8)) {
                    val stroke = Stroke(width = strokeWidth.toPx())
                    drawArc(color = trackColor, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = stroke)
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedValue,
                        useCenter = false,
                        style = stroke,
                    )
                }
            }
        }
        Spacer(Modifier.height(OmniTheme.spacing.spazio1))
        BasicText(text = label, style = OmniTheme.typography.didascalia.copy(color = OmniTheme.colors.testoSecondario))
    }
}
