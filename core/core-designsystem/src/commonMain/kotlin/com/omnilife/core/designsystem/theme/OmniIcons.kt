package com.omnilife.core.designsystem.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * A single monochromatic, single-stroke icon set (DS-05/DS-09) — hand-drawn
 * with [DrawScope] primitives rather than imported as a third-party icon
 * font/library (TDR-22): every glyph drawn with the same stroke
 * width/cap/join, guaranteeing "one set, one stroke style" by construction
 * rather than by curation. NOTIFICATIONS/SYNC added in Sprint 4 for the
 * Home Dashboard's notification-center entry and sync-status indicator.
 */
public enum class OmniIconType {
    CHECK,
    CLOSE,
    CHEVRON_BACK,
    CHEVRON_FORWARD,
    MORE_HORIZONTAL,
    SEARCH,
    ADD,
    ERROR,
    WARNING,
    INFO,
    ARROW_BACK,
    NOTIFICATIONS,
    SYNC,
}

/**
 * Renders an [OmniIconType]. `contentDescription` is required to be
 * non-null whenever this icon is the sole carrier of meaning for an action
 * (DS-07); pass `null` only when adjacent visible text already describes
 * the action, which hides the icon from screen readers as purely
 * decorative.
 */
@Suppress("CyclomaticComplexMethod") // One branch per OmniIconType, each a single trivial draw call —
// splitting this into a lookup table would launder the metric, not simplify the code.
@Composable
public fun OmniIcon(
    type: OmniIconType,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val a11yModifier =
        if (contentDescription != null) {
            Modifier.semantics { this.contentDescription = contentDescription }
        } else {
            Modifier.clearAndSetSemantics {}
        }
    Canvas(modifier = modifier.size(24.dp).then(a11yModifier)) {
        val stroke = Stroke(width = size.minDimension * 0.083f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (type) {
            OmniIconType.CHECK -> drawCheck(stroke, tint)
            OmniIconType.CLOSE -> drawClose(stroke, tint)
            OmniIconType.CHEVRON_BACK -> drawChevron(stroke, tint, pointingRight = false)
            OmniIconType.CHEVRON_FORWARD -> drawChevron(stroke, tint, pointingRight = true)
            OmniIconType.MORE_HORIZONTAL -> drawMoreHorizontal(tint)
            OmniIconType.SEARCH -> drawSearch(stroke, tint)
            OmniIconType.ADD -> drawAdd(stroke, tint)
            // Distinct shapes, not just distinct tint (DS-08/DS-INV-05): a
            // triangle for "attenzione" (the universal caution glyph), a
            // circle for "critico" — never the same silhouette recolored.
            OmniIconType.ERROR -> drawExclamationGlyph(stroke, tint, circular = true)
            OmniIconType.WARNING -> drawExclamationGlyph(stroke, tint, circular = false)
            OmniIconType.INFO -> drawInfo(stroke, tint)
            OmniIconType.ARROW_BACK -> drawArrowBack(stroke, tint)
            OmniIconType.NOTIFICATIONS -> drawNotifications(stroke, tint)
            OmniIconType.SYNC -> drawSync(stroke, tint)
        }
    }
}

private fun DrawScope.drawCheck(
    stroke: Stroke,
    tint: Color,
) {
    val w = size.width
    val h = size.height
    val path =
        androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.2f, h * 0.55f)
            lineTo(w * 0.42f, h * 0.75f)
            lineTo(w * 0.82f, h * 0.28f)
        }
    drawPath(path, tint, style = stroke)
}

private fun DrawScope.drawClose(
    stroke: Stroke,
    tint: Color,
) {
    val w = size.width
    val h = size.height
    drawLine(tint, Offset(w * 0.25f, h * 0.25f), Offset(w * 0.75f, h * 0.75f), stroke.width, stroke.cap)
    drawLine(tint, Offset(w * 0.75f, h * 0.25f), Offset(w * 0.25f, h * 0.75f), stroke.width, stroke.cap)
}

private fun DrawScope.drawChevron(
    stroke: Stroke,
    tint: Color,
    pointingRight: Boolean,
) {
    val w = size.width
    val h = size.height
    val xStart = if (pointingRight) w * 0.4f else w * 0.6f
    val xEnd = if (pointingRight) w * 0.6f else w * 0.4f
    val path =
        androidx.compose.ui.graphics.Path().apply {
            moveTo(xStart, h * 0.28f)
            lineTo(xEnd, h * 0.5f)
            lineTo(xStart, h * 0.72f)
        }
    drawPath(path, tint, style = stroke)
}

private fun DrawScope.drawMoreHorizontal(tint: Color) {
    val h = size.height
    val r = size.minDimension * 0.06f
    listOf(0.2f, 0.5f, 0.8f).forEach { fraction ->
        drawCircle(tint, radius = r, center = Offset(size.width * fraction, h * 0.5f))
    }
}

private fun DrawScope.drawSearch(
    stroke: Stroke,
    tint: Color,
) {
    val center = Offset(size.width * 0.42f, size.height * 0.42f)
    val radius = size.minDimension * 0.28f
    drawCircle(tint, radius = radius, center = center, style = stroke)
    val handleStart = Offset(center.x + radius * 0.75f, center.y + radius * 0.75f)
    val handleEnd = Offset(size.width * 0.85f, size.height * 0.85f)
    drawLine(tint, handleStart, handleEnd, stroke.width, stroke.cap)
}

private fun DrawScope.drawAdd(
    stroke: Stroke,
    tint: Color,
) {
    val w = size.width
    val h = size.height
    drawLine(tint, Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.78f), stroke.width, stroke.cap)
    drawLine(tint, Offset(w * 0.22f, h * 0.5f), Offset(w * 0.78f, h * 0.5f), stroke.width, stroke.cap)
}

private fun DrawScope.drawExclamationGlyph(
    stroke: Stroke,
    tint: Color,
    circular: Boolean,
) {
    val w = size.width
    val h = size.height
    val radius = size.minDimension * 0.4f
    if (circular) {
        drawCircle(tint, radius = radius, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
    } else {
        val path =
            androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.5f, h * 0.18f)
                lineTo(w * 0.14f, h * 0.82f)
                lineTo(w * 0.86f, h * 0.82f)
                close()
            }
        drawPath(path, tint, style = stroke)
    }
    drawLine(tint, Offset(w * 0.5f, h * 0.4f), Offset(w * 0.5f, h * 0.62f), stroke.width, stroke.cap)
    drawCircle(tint, radius = stroke.width * 0.6f, center = Offset(w * 0.5f, h * 0.72f))
}

private fun DrawScope.drawInfo(
    stroke: Stroke,
    tint: Color,
) {
    val w = size.width
    val h = size.height
    val radius = size.minDimension * 0.4f
    drawCircle(tint, radius = radius, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
    drawCircle(tint, radius = stroke.width * 0.6f, center = Offset(w * 0.5f, h * 0.32f))
    drawLine(tint, Offset(w * 0.5f, h * 0.45f), Offset(w * 0.5f, h * 0.68f), stroke.width, stroke.cap)
}

private fun DrawScope.drawArrowBack(
    stroke: Stroke,
    tint: Color,
) {
    val w = size.width
    val h = size.height
    drawLine(tint, Offset(w * 0.8f, h * 0.5f), Offset(w * 0.2f, h * 0.5f), stroke.width, stroke.cap)
    val arrowHead =
        androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.42f, h * 0.28f)
            lineTo(w * 0.2f, h * 0.5f)
            lineTo(w * 0.42f, h * 0.72f)
        }
    drawPath(arrowHead, tint, style = stroke)
}

private fun DrawScope.drawNotifications(
    stroke: Stroke,
    tint: Color,
) {
    val w = size.width
    val h = size.height
    val bellPath =
        androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, h * 0.16f)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(w * 0.24f, h * 0.2f, w * 0.76f, h * 0.72f),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false,
            )
            lineTo(w * 0.8f, h * 0.72f)
            lineTo(w * 0.2f, h * 0.72f)
            close()
        }
    drawPath(bellPath, tint, style = stroke)
    drawLine(tint, Offset(w * 0.4f, h * 0.8f), Offset(w * 0.6f, h * 0.8f), stroke.width, stroke.cap)
}

private fun DrawScope.drawSync(
    stroke: Stroke,
    tint: Color,
) {
    val center = Offset(size.width * 0.5f, size.height * 0.5f)
    val radius = size.minDimension * 0.3f
    drawArc(
        color = tint,
        startAngle = -30f,
        sweepAngle = 260f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = stroke,
    )
    val arrowTip = pointOnCircle(center, radius, angleRadians = (-30f) * (kotlin.math.PI.toFloat() / 180f))
    val arrowHead =
        androidx.compose.ui.graphics.Path().apply {
            moveTo(arrowTip.x - radius * 0.35f, arrowTip.y - radius * 0.35f)
            lineTo(arrowTip.x, arrowTip.y)
            lineTo(arrowTip.x - radius * 0.5f, arrowTip.y + radius * 0.15f)
        }
    drawPath(arrowHead, tint, style = stroke)
}

// Used by drawSync's arrowhead placement (Sprint 4) — shared here rather than duplicated
// wherever a circular layout needs a point on an arc.
internal fun pointOnCircle(
    center: Offset,
    radius: Float,
    angleRadians: Float,
): Offset = Offset(center.x + radius * cos(angleRadians), center.y + radius * sin(angleRadians))
