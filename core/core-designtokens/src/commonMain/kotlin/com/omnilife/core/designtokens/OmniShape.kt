package com.omnilife.core.designtokens

/**
 * Border/radius scale (Design System Bible §01-token-visivi §5). Radii are
 * derived from [OmniSpacing] ("coerente con", not a fixed ratio in the
 * Bible) by halving the corresponding spacing token — kept on the same
 * scale (DS-INV-02) without producing pill shapes on Card/TextField
 * (TDR-22). `raggioPieno` is relative (50% of a component's own height/
 * width) and has no fixed dp value — modeled as a shape, not a token, in
 * core-designsystem.
 */
public object OmniShape {
    public const val RAGGIO_PICCOLO_DP: Float = OmniSpacing.SPAZIO_1 / 2f
    public const val RAGGIO_MEDIO_DP: Float = OmniSpacing.SPAZIO_2 / 2f
    public const val RAGGIO_GRANDE_DP: Float = OmniSpacing.SPAZIO_4 / 2f
}

/** Border thickness scale (Design System Bible §01-token-visivi §5). */
public object OmniBorder {
    public const val SPESSORE_DEFAULT_DP: Float = 1f
    public const val SPESSORE_FOCUS_DP: Float = SPESSORE_DEFAULT_DP * 2f
}

/** 4-level elevation scale (Design System Bible §01-token-visivi §4, new — not defined in earlier Bibles). */
public enum class OmniElevationLevel(public val shadowDp: Float) {
    LIVELLO_0(shadowDp = 0f),
    LIVELLO_1(shadowDp = 1f),
    LIVELLO_2(shadowDp = 4f),
    LIVELLO_3(shadowDp = 8f),
}

/** Opacity scale (Design System Bible §01-token-visivi §6). */
public object OmniOpacity {
    public const val DISABILITATO: Float = 0.38f
    public const val ARCHIVIATO: Float = 0.70f
    public const val SCRIM: Float = 0.50f
    public const val COMPLETATO: Float = 0.60f
}
