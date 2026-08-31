package com.omnilife.domain.account

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test di fumo originario del bootstrap del modulo. Il comportamento reale (Setting, Onboarding)
 * ha ora una propria suite — vedi SettingsUseCasesTest e SqlDelightSettingsRepositoryTest.
 */
class AccountSmokeTest {
    @Test
    fun moduleCompiles() {
        assertTrue(true, "domain:domain-account module scaffold is in place")
    }
}
