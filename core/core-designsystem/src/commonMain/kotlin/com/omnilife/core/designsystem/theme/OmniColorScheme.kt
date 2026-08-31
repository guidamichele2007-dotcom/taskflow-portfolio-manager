package com.omnilife.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.omnilife.core.designtokens.OmniAccent
import com.omnilife.core.designtokens.OmniColor
import com.omnilife.core.designtokens.OmniColorPair
import com.omnilife.core.designtokens.OmniColors

private fun OmniColor.toComposeColor(): Color {
    val value = hex.removePrefix("#").toLong(16)
    return Color(0xFF000000L.or(value))
}

private fun OmniColorPair.resolve(darkTheme: Boolean): Color = (if (darkTheme) dark else light).toComposeColor()

/** Every color role a component may read — resolved for one theme (light or dark), never both at once. */
@Immutable
public data class OmniColorScheme(
    public val superficieBase: Color,
    public val superficieElevata: Color,
    public val superficieOverlay: Color,
    public val testoPrimario: Color,
    public val testoSecondario: Color,
    public val testoSuAccento: Color,
    public val bordoDefault: Color,
    public val bordoFocus: Color,
    public val accento: Color,
    public val statoPositivoSobrio: Color,
    public val statoAttenzione: Color,
    public val statoCritico: Color,
    public val statoInformativo: Color,
)

internal fun buildOmniColorScheme(
    darkTheme: Boolean,
    accent: OmniAccent,
): OmniColorScheme =
    OmniColorScheme(
        superficieBase = OmniColors.superficieBase.resolve(darkTheme),
        superficieElevata = OmniColors.superficieElevata.resolve(darkTheme),
        superficieOverlay = OmniColors.superficieOverlay.resolve(darkTheme),
        testoPrimario = OmniColors.testoPrimario.resolve(darkTheme),
        testoSecondario = OmniColors.testoSecondario.resolve(darkTheme),
        testoSuAccento = OmniColors.testoSuAccento.resolve(darkTheme),
        bordoDefault = OmniColors.bordoDefault.resolve(darkTheme),
        bordoFocus = OmniColors.bordoFocus.resolve(darkTheme),
        accento = accent.pair.resolve(darkTheme),
        statoPositivoSobrio = OmniColors.statoPositivoSobrio.resolve(darkTheme),
        statoAttenzione = OmniColors.statoAttenzione.resolve(darkTheme),
        statoCritico = OmniColors.statoCritico.resolve(darkTheme),
        statoInformativo = OmniColors.statoInformativo.resolve(darkTheme),
    )
