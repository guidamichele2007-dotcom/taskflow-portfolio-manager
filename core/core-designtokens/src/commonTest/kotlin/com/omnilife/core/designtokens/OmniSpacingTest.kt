package com.omnilife.core.designtokens

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Design System Bible §01 §1: spazio1 must equal 1/4 of the minimum touch
 * target (DS-34), every other step a multiple of unitDp.
 */
class OmniSpacingTest {
    @Test
    fun `spazio1 equals one quarter of the minimum touch target`() {
        assertEquals(OmniTouchTarget.MINIMO_DP / 4f, OmniSpacing.SPAZIO_1)
    }

    @Test
    fun `every spacing step is a whole multiple of unitDp`() {
        assertEquals(0.5f, OmniSpacing.SPAZIO_05 / OmniSpacing.UNIT_DP)
        assertEquals(1f, OmniSpacing.SPAZIO_1 / OmniSpacing.UNIT_DP)
        assertEquals(2f, OmniSpacing.SPAZIO_2 / OmniSpacing.UNIT_DP)
        assertEquals(3f, OmniSpacing.SPAZIO_3 / OmniSpacing.UNIT_DP)
        assertEquals(4f, OmniSpacing.SPAZIO_4 / OmniSpacing.UNIT_DP)
        assertEquals(6f, OmniSpacing.SPAZIO_6 / OmniSpacing.UNIT_DP)
        assertEquals(8f, OmniSpacing.SPAZIO_8 / OmniSpacing.UNIT_DP)
    }
}
