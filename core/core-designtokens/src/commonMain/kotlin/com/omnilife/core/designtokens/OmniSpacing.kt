package com.omnilife.core.designtokens

/**
 * Spacing scale (Design System Bible §01-token-visivi §1). Values are in dp
 * (TDR-22 — the Bible itself only fixes ratios, never a physical unit).
 *
 * `unitDp` is not an arbitrary choice: the Bible requires `spazio1` to equal
 * one quarter of the minimum touch target (DS-34, [OmniTouchTarget]), so
 * `unitDp = OmniTouchTarget.minimoDp / 4`.
 */
public object OmniSpacing {
    public const val UNIT_DP: Float = OmniTouchTarget.MINIMO_DP / 4f

    public const val SPAZIO_05: Float = UNIT_DP * 0.5f
    public const val SPAZIO_1: Float = UNIT_DP * 1f
    public const val SPAZIO_2: Float = UNIT_DP * 2f
    public const val SPAZIO_3: Float = UNIT_DP * 3f
    public const val SPAZIO_4: Float = UNIT_DP * 4f
    public const val SPAZIO_6: Float = UNIT_DP * 6f
    public const val SPAZIO_8: Float = UNIT_DP * 8f
}

/** Minimum interactive touch target (Design System Bible DS-34: 44pt/48dp). */
public object OmniTouchTarget {
    public const val MINIMO_DP: Float = 48f
}
