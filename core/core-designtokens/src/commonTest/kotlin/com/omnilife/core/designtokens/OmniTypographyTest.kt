package com.omnilife.core.designtokens

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Design System Bible §01 §2: 7-level scale, ratios relative to corpo.default, DS-01 line-height rule. */
class OmniTypographyTest {
    private val body = OmniTypeLevel.CORPO_DEFAULT.sizeSp

    @Test
    fun `every level matches its documented ratio to corpo default`() {
        assertEquals(2.0f, OmniTypeLevel.TITOLO_GRANDE.sizeSp / body)
        assertEquals(1.5f, OmniTypeLevel.TITOLO_SCHERMATA.sizeSp / body)
        assertEquals(1.25f, OmniTypeLevel.TITOLO_SEZIONE.sizeSp / body)
        assertEquals(1.0f, OmniTypeLevel.CORPO_ENFATIZZATO.sizeSp / body)
        assertEquals(1.0f, OmniTypeLevel.CORPO_DEFAULT.sizeSp / body)
        assertEquals(0.875f, OmniTypeLevel.ETICHETTA.sizeSp / body)
        assertEquals(0.75f, OmniTypeLevel.DIDASCALIA.sizeSp / body)
    }

    @Test
    fun `DS-01 - regular levels keep at least 1point4x line height`() {
        val regularLevels =
            listOf(
                OmniTypeLevel.TITOLO_SEZIONE,
                OmniTypeLevel.CORPO_ENFATIZZATO,
                OmniTypeLevel.CORPO_DEFAULT,
                OmniTypeLevel.ETICHETTA,
                OmniTypeLevel.DIDASCALIA,
            )
        regularLevels.forEach { level ->
            assertTrue(
                level.lineHeightSp / level.sizeSp >= 1.4f,
                "${level.name} line-height ratio ${level.lineHeightSp / level.sizeSp} < 1.4",
            )
        }
    }

    @Test
    fun `DS-01 - the two largest levels reduce line height to 1point15-1point25x`() {
        listOf(OmniTypeLevel.TITOLO_GRANDE, OmniTypeLevel.TITOLO_SCHERMATA).forEach { level ->
            val ratio = level.lineHeightSp / level.sizeSp
            assertTrue(ratio in 1.15f..1.25f, "${level.name} line-height ratio $ratio outside 1.15-1.25")
        }
    }

    @Test
    fun `scale has exactly the 7 documented levels`() {
        assertEquals(7, OmniTypeLevel.entries.size)
    }
}
