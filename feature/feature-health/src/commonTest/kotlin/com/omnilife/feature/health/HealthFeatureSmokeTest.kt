package com.omnilife.feature.health

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test di fumo: verifica solo che il modulo compili e il suo grafo di
 * dipendenze sia risolvibile. Nessun test di comportamento (nessuna
 * logica di business esiste ancora in questo modulo).
 */
class HealthFeatureSmokeTest {
    @Test
    fun moduleCompiles() {
        assertTrue(true, "feature:feature-health module scaffold is in place")
    }
}
