package com.omnilife.core.designtokens

import kotlin.math.pow

/** WCAG 2.1 minimum contrast ratios (Design System Bible §04 §3). */
public object WcagContrast {
    public const val AA_NORMAL_TEXT: Double = 4.5
    public const val AA_UI_COMPONENT: Double = 3.0
}

/**
 * WCAG 2.1 relative-luminance contrast ratio between two [OmniColor]s
 * (DS-26: every color pair must be verified independently per theme — this
 * is the mechanical verification, not a one-off manual check).
 */
public object ColorContrast {
    public fun ratio(
        a: OmniColor,
        b: OmniColor,
    ): Double {
        val la = relativeLuminance(a)
        val lb = relativeLuminance(b)
        val lighter = maxOf(la, lb)
        val darker = minOf(la, lb)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun relativeLuminance(color: OmniColor): Double {
        val hex = color.hex.removePrefix("#")
        val r = hex.substring(0, 2).toInt(16)
        val g = hex.substring(2, 4).toInt(16)
        val b = hex.substring(4, 6).toInt(16)
        return 0.2126 * linearize(r) + 0.7152 * linearize(g) + 0.0722 * linearize(b)
    }

    private fun linearize(channel: Int): Double {
        val c = channel / 255.0
        return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }
}
