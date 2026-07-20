package com.omnilife.domain.habit

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test di fumo: verifica solo che il modulo compili e il suo grafo di
 * dipendenze sia risolvibile. Nessun test di comportamento (nessuna
 * logica di business esiste ancora in questo modulo).
 */
class HabitSmokeTest {
    @Test
    fun moduleCompiles() {
        assertTrue(true, "domain:domain-habit module scaffold is in place")
    }
}
