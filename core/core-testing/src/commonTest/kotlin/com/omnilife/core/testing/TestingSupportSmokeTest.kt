package com.omnilife.core.testing

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test di fumo: verifica solo che il modulo compili e il suo grafo di
 * dipendenze sia risolvibile. Nessun test di comportamento (nessuna
 * logica di business esiste ancora in questo modulo).
 */
class TestingSupportSmokeTest {
    @Test
    fun moduleCompiles() {
        assertTrue(true, "core:core-testing module scaffold is in place")
    }
}
