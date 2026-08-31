package com.omnilife.core.designtokens

/**
 * The 9 motion tokens the Design System Bible allows, no more (DS-28): the
 * 4 durations/curves already normative in UX Bible MUC §4 (not redefined,
 * only referenced) plus the 5 visual-only tokens new to
 * [03-motion](../../../../../docs/omnilife/design_system_bible/03-motion.md).
 * Every animation in `core-designsystem` must map to exactly one of these.
 */
public enum class OmniMotionToken {
    MICRO,
    STANDARD,
    ENFASI,
    USCITA,
    ELEVAZIONE_TRANSIZIONE,
    SHIMMER,
    SCALA_PRESSIONE,
    SCALA_TRASCINAMENTO,
    PROGRESSIONE,
}

/**
 * Durations in ms for the 4 UX Bible MUC §4 tokens (picked at the midpoint
 * of each documented range, TDR-22).
 */
public object OmniMotionDurationMs {
    public const val MICRO: Int = 120
    public const val STANDARD: Int = 220
    public const val ENFASI: Int = 330
    public const val USCITA: Int = 180
}

/**
 * Scale-transform tokens (Design System Bible §03 §3): press feedback and
 * drag lift, both a few percent, never more.
 */
public object OmniMotionScale {
    public const val PRESSIONE: Float = 0.97f
    public const val TRASCINAMENTO: Float = 1.03f
}
