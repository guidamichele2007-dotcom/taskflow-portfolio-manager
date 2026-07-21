package com.omnilife.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.omnilife.core.designtokens.OmniFontWeightRole
import com.omnilife.core.designtokens.OmniTypeLevel

private fun OmniFontWeightRole.toFontWeight(): FontWeight =
    when (this) {
        OmniFontWeightRole.DECISO -> FontWeight.Bold
        OmniFontWeightRole.ENFATIZZATO -> FontWeight.Medium
        OmniFontWeightRole.REGOLARE -> FontWeight.Normal
    }

private fun OmniTypeLevel.toTextStyle(): TextStyle =
    TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = weight.toFontWeight(),
        fontSize = sizeSp.sp,
        lineHeight = lineHeightSp.sp,
    )

/** One [TextStyle] per [OmniTypeLevel] (Design System Bible §01 §2) — the only text styles a component may use. */
@Immutable
public data class OmniTypographyStyles(
    public val titoloGrande: TextStyle,
    public val titoloSchermata: TextStyle,
    public val titoloSezione: TextStyle,
    public val corpoEnfatizzato: TextStyle,
    public val corpoDefault: TextStyle,
    public val etichetta: TextStyle,
    public val didascalia: TextStyle,
)

internal fun buildOmniTypographyStyles(): OmniTypographyStyles =
    OmniTypographyStyles(
        titoloGrande = OmniTypeLevel.TITOLO_GRANDE.toTextStyle(),
        titoloSchermata = OmniTypeLevel.TITOLO_SCHERMATA.toTextStyle(),
        titoloSezione = OmniTypeLevel.TITOLO_SEZIONE.toTextStyle(),
        corpoEnfatizzato = OmniTypeLevel.CORPO_ENFATIZZATO.toTextStyle(),
        corpoDefault = OmniTypeLevel.CORPO_DEFAULT.toTextStyle(),
        etichetta = OmniTypeLevel.ETICHETTA.toTextStyle(),
        didascalia = OmniTypeLevel.DIDASCALIA.toTextStyle(),
    )
