package com.omnilife.core.designtokens

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * DS-26: every color pair verified independently against both themes, not
 * assumed valid in dark because it passed in light. A future change to any
 * hex value that regresses contrast fails this test, not a design review.
 */
class ColorContrastTest {
    @Test
    fun `body text meets AA 4point5 to 1 against its surface, both themes`() {
        assertAaNormalText(OmniColors.testoPrimario, OmniColors.superficieBase)
        assertAaNormalText(OmniColors.testoSecondario, OmniColors.superficieBase)
        assertAaNormalText(OmniColors.testoPrimario, OmniColors.superficieElevata)
        assertAaNormalText(OmniColors.testoSecondario, OmniColors.superficieElevata)
    }

    @Test
    fun `focus border meets AA 3 to 1 UI contrast against its surface, both themes`() {
        assertAaUiComponent(OmniColors.bordoFocus, OmniColors.superficieBase)
    }

    @Test
    fun `semantic state colors meet AA 4point5 to 1 against the base surface, both themes`() {
        assertAaNormalText(OmniColors.statoPositivoSobrio, OmniColors.superficieBase)
        assertAaNormalText(OmniColors.statoAttenzione, OmniColors.superficieBase)
        assertAaNormalText(OmniColors.statoCritico, OmniColors.superficieBase)
        assertAaNormalText(OmniColors.statoInformativo, OmniColors.superficieBase)
    }

    @Test
    fun `every accent option meets AA 4point5 to 1 against testo su accento`() {
        OmniAccent.entries.forEach { accent ->
            val ratioLight = ColorContrast.ratio(accent.pair.light, OmniColors.testoSuAccento.light)
            val ratioDark = ColorContrast.ratio(accent.pair.dark, OmniColors.testoSuAccento.dark)
            assertTrue(ratioLight >= WcagContrast.AA_NORMAL_TEXT, "${accent.name} light: $ratioLight < 4.5")
            assertTrue(ratioDark >= WcagContrast.AA_NORMAL_TEXT, "${accent.name} dark: $ratioDark < 4.5")
        }
    }

    @Test
    fun `accent set is closed at exactly 6 options, per Functional Bible SET-001`() {
        assertTrue(OmniAccent.entries.size == 6)
    }

    private fun assertAaNormalText(
        foreground: OmniColorPair,
        background: OmniColorPair,
    ) {
        val light = ColorContrast.ratio(foreground.light, background.light)
        val dark = ColorContrast.ratio(foreground.dark, background.dark)
        assertTrue(light >= WcagContrast.AA_NORMAL_TEXT, "light: $light < ${WcagContrast.AA_NORMAL_TEXT}")
        assertTrue(dark >= WcagContrast.AA_NORMAL_TEXT, "dark: $dark < ${WcagContrast.AA_NORMAL_TEXT}")
    }

    private fun assertAaUiComponent(
        foreground: OmniColorPair,
        background: OmniColorPair,
    ) {
        val light = ColorContrast.ratio(foreground.light, background.light)
        val dark = ColorContrast.ratio(foreground.dark, background.dark)
        assertTrue(light >= WcagContrast.AA_UI_COMPONENT, "light: $light < ${WcagContrast.AA_UI_COMPONENT}")
        assertTrue(dark >= WcagContrast.AA_UI_COMPONENT, "dark: $dark < ${WcagContrast.AA_UI_COMPONENT}")
    }
}
