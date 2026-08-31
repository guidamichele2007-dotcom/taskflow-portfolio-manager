package com.omnilife.core.designtokens

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * DS-28: exactly 9 motion tokens, none of which exceeds the 350ms ceiling
 * shared by every animation (Design System Bible §03 §1).
 */
class OmniMotionTest {
    @Test
    fun `exactly 9 motion tokens exist`() {
        assertEquals(9, OmniMotionToken.entries.size)
    }

    @Test
    fun `every duration-bearing token stays within its documented UX Bible MUC range`() {
        assertTrue(OmniMotionDurationMs.MICRO in 100..150)
        assertTrue(OmniMotionDurationMs.STANDARD in 200..250)
        assertTrue(OmniMotionDurationMs.ENFASI in 300..350)
        assertTrue(OmniMotionDurationMs.USCITA in 150..200)
    }

    @Test
    fun `no animation exceeds the 350ms ceiling (UX Bible MUC section 4)`() {
        listOf(
            OmniMotionDurationMs.MICRO,
            OmniMotionDurationMs.STANDARD,
            OmniMotionDurationMs.ENFASI,
            OmniMotionDurationMs.USCITA,
        ).forEach { duration -> assertTrue(duration <= 350, "$duration ms exceeds the 350ms ceiling") }
    }

    @Test
    fun `scale tokens are a few percent, never a dramatic transform`() {
        assertTrue(OmniMotionScale.PRESSIONE in 0.95f..0.99f)
        assertTrue(OmniMotionScale.TRASCINAMENTO in 1.01f..1.05f)
    }
}
