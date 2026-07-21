package com.omnilife.core.designtokens

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Design System Bible §01 §5: radii derived from the spacing scale (DS-INV-02), focus border 2x the default. */
class OmniShapeTest {
    @Test
    fun `radii are derived from the spacing scale, never a value outside it`() {
        assertEquals(OmniSpacing.SPAZIO_1 / 2f, OmniShape.RAGGIO_PICCOLO_DP)
        assertEquals(OmniSpacing.SPAZIO_2 / 2f, OmniShape.RAGGIO_MEDIO_DP)
        assertEquals(OmniSpacing.SPAZIO_4 / 2f, OmniShape.RAGGIO_GRANDE_DP)
    }

    @Test
    fun `focus border is exactly 2x the default border`() {
        assertEquals(OmniBorder.SPESSORE_DEFAULT_DP * 2f, OmniBorder.SPESSORE_FOCUS_DP)
    }

    @Test
    fun `elevation has exactly the 4 documented levels, strictly increasing`() {
        val levels = OmniElevationLevel.entries
        assertEquals(4, levels.size)
        levels.zipWithNext().forEach { (a, b) -> assertTrue(a.shadowDp < b.shadowDp, "$a >= $b") }
    }
}
