package com.omnilife.domain.calendar

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test di fumo: verifica solo che il modulo compili e il suo grafo di
 * dipendenze sia risolvibile. Nessun test di comportamento (nessuna
 * logica di business esiste ancora in questo modulo).
 */
class TimeBoxSmokeTest {
    @Test
    fun moduleCompiles() {
        assertTrue(true, "domain:domain-calendar module scaffold is in place")
    }
}
