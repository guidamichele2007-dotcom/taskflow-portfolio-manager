package com.omnilife.core.designtokens

/**
 * A single sRGB color, `#RRGGBB`. Deliberately not a platform graphics
 * type (Design System Bible §00: token layer stays framework-agnostic) —
 * core-designsystem converts this to `androidx.compose.ui.graphics.Color`.
 */
public data class OmniColor(public val hex: String) {
    init {
        require(HEX_PATTERN.matches(hex)) { "OmniColor must be #RRGGBB, was '$hex'" }
    }

    public companion object {
        private val HEX_PATTERN = Regex("^#[0-9A-Fa-f]{6}$")
    }
}

/**
 * A color role defined as an explicit light/dark pair (DS-INV-01: never one
 * value derived automatically from the other).
 */
public data class OmniColorPair(public val light: OmniColor, public val dark: OmniColor)

/**
 * Color roles, not module palettes (P55/UX-C-291, Design System Bible §01
 * §3) — every pair independently chosen and contrast-verified for both
 * themes (DS-26), see [ColorContrastTest][core-designtokens commonTest].
 */
public object OmniColors {
    // Surface & content roles (§3.1)
    public val superficieBase: OmniColorPair = OmniColorPair(OmniColor("#FAFAFA"), OmniColor("#121212"))
    public val superficieElevata: OmniColorPair = OmniColorPair(OmniColor("#FFFFFF"), OmniColor("#1E1E1E"))

    // Scrim behind an open sheet/dialog (opacity applied separately via OmniOpacity.SCRIM):
    // deliberately the same explicit value in both themes, not derived.
    public val superficieOverlay: OmniColorPair = OmniColorPair(OmniColor("#000000"), OmniColor("#000000"))

    public val testoPrimario: OmniColorPair = OmniColorPair(OmniColor("#1A1A1A"), OmniColor("#F2F2F2"))
    public val testoSecondario: OmniColorPair = OmniColorPair(OmniColor("#5C5C5C"), OmniColor("#ADADAD"))

    // Text placed on top of accento.base (buttons, filled chips). accento.base
    // keeps one saturated value across both themes (see OmniAccent + TDR-22),
    // so a single white works reliably in both themes.
    public val testoSuAccento: OmniColorPair = OmniColorPair(OmniColor("#FFFFFF"), OmniColor("#FFFFFF"))

    public val bordoDefault: OmniColorPair = OmniColorPair(OmniColor("#E0E0E0"), OmniColor("#3A3A3A"))

    // Independent of the chosen accent (DS-32): a fixed, always-AA-contrast
    // focus ring color, so focus visibility never depends on which accent
    // the user picked.
    public val bordoFocus: OmniColorPair = OmniColorPair(OmniColor("#1A1A1A"), OmniColor("#F2F2F2"))

    // Semantic (state, not module) roles (§3.2)
    public val statoPositivoSobrio: OmniColorPair = OmniColorPair(OmniColor("#2E6E4F"), OmniColor("#7FCBA3"))
    public val statoAttenzione: OmniColorPair = OmniColorPair(OmniColor("#8A6516"), OmniColor("#E4B34E"))
    public val statoCritico: OmniColorPair = OmniColorPair(OmniColor("#B3261E"), OmniColor("#F2B8B5"))
    public val statoInformativo: OmniColorPair = OmniColorPair(OmniColor("#2E5A78"), OmniColor("#9DC4E3"))
}

/**
 * `accento.base`'s closed set of user-selectable values (Functional Bible
 * SET-001 §2 "Aspetto"; Design System Bible §01 §3.2: one active value at a
 * time, never a per-module palette). Hues picked to stay visually distinct
 * from [OmniColors.statoAttenzione] (amber) and [OmniColors.statoCritico]
 * (red) — an accent must never be mistaken for a semantic state color
 * (DS-INV-05).
 */
public enum class OmniAccent(public val pair: OmniColorPair) {
    BLU(OmniColorPair(OmniColor("#2255A4"), OmniColor("#2255A4"))),
    VERDE(OmniColorPair(OmniColor("#2E7D5B"), OmniColor("#2E7D5B"))),
    VIOLA(OmniColorPair(OmniColor("#6A4C93"), OmniColor("#6A4C93"))),
    CORALLO(OmniColorPair(OmniColor("#B5502C"), OmniColor("#B5502C"))),
    PETROLIO(OmniColorPair(OmniColor("#1B6C70"), OmniColor("#1B6C70"))),
    INDACO(OmniColorPair(OmniColor("#3D4E9E"), OmniColor("#3D4E9E"))),
}
