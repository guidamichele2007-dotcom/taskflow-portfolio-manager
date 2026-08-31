package com.omnilife.platform.storage

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test di fumo: verifica solo che il modulo compili e il suo grafo di
 * dipendenze sia risolvibile. Nessun test di comportamento (nessuna
 * logica di business esiste ancora in questo modulo).
 */
class FileStorageAdapterSmokeTest {
    @Test
    fun moduleCompiles() {
        assertTrue(true, "platform:platform-storage module scaffold is in place")
    }
}
