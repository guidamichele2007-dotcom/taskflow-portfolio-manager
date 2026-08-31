package com.omnilife.core.designtokens

/**
 * One of the 3 type weights the Bible names (§01-token-visivi §2) — mapped
 * to a platform `FontWeight` in `core-designsystem`.
 */
public enum class OmniFontWeightRole {
    DECISO,
    ENFATIZZATO,
    REGOLARE,
}

/**
 * The 7-level type scale (Design System Bible §01-token-visivi §2,
 * UX-C-296). Sizes are in sp, base body = 16sp (TDR-22 — the Bible fixes
 * only the ratios, not a physical size). Line heights follow DS-01 (>=1.4x
 * body; 1.15-1.25x for the two largest levels only).
 */
public enum class OmniTypeLevel(
    public val sizeSp: Float,
    public val lineHeightSp: Float,
    public val weight: OmniFontWeightRole,
) {
    TITOLO_GRANDE(sizeSp = 32f, lineHeightSp = 38f, weight = OmniFontWeightRole.DECISO),
    TITOLO_SCHERMATA(sizeSp = 24f, lineHeightSp = 29f, weight = OmniFontWeightRole.DECISO),
    TITOLO_SEZIONE(sizeSp = 20f, lineHeightSp = 29f, weight = OmniFontWeightRole.DECISO),
    CORPO_ENFATIZZATO(sizeSp = 16f, lineHeightSp = 23f, weight = OmniFontWeightRole.ENFATIZZATO),
    CORPO_DEFAULT(sizeSp = 16f, lineHeightSp = 23f, weight = OmniFontWeightRole.REGOLARE),
    ETICHETTA(sizeSp = 14f, lineHeightSp = 20f, weight = OmniFontWeightRole.REGOLARE),
    DIDASCALIA(sizeSp = 12f, lineHeightSp = 17f, weight = OmniFontWeightRole.REGOLARE),
}
