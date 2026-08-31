package com.omnilife.core.security

import com.omnilife.core.common.OmniResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class KeyManagerTest {
    @Test
    fun `setUpMasterKey unlocks immediately and unlockWithPassphrase succeeds afterwards`() {
        val storage = InMemorySecureStorage()
        val keyManager = KeyManager(storage = storage)

        keyManager.setUpMasterKey("correct horse battery staple".toCharArray())
        assertTrue(keyManager.isUnlocked())

        keyManager.lock()
        assertFalse(keyManager.isUnlocked())

        val result = keyManager.unlockWithPassphrase("correct horse battery staple".toCharArray())
        assertIs<OmniResult.Success<Unit>>(result)
        assertTrue(keyManager.isUnlocked())
    }

    @Test
    fun `unlockWithPassphrase fails with the wrong passphrase`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("right passphrase".toCharArray())
        keyManager.lock()

        val result = keyManager.unlockWithPassphrase("wrong passphrase".toCharArray())

        assertIs<OmniResult.Failure>(result)
        assertEquals(SecurityError.InvalidPassphrase, result.error)
        assertFalse(keyManager.isUnlocked())
    }

    @Test
    fun `unlockWithRecoveryPhrase succeeds with the phrase generated at setup (SEC-002)`() {
        val keyManager = KeyManager()
        val recoveryPhrase = keyManager.setUpMasterKey("right passphrase".toCharArray())
        keyManager.lock()

        val result = keyManager.unlockWithRecoveryPhrase(recoveryPhrase)

        assertIs<OmniResult.Success<Unit>>(result)
        assertTrue(keyManager.isUnlocked())
    }

    @Test
    fun `unlockWithRecoveryPhrase fails with a different phrase (SEC-AC-02)`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("right passphrase".toCharArray())
        keyManager.lock()
        val wrongPhrase = RecoveryKeyService().generate()

        val result = keyManager.unlockWithRecoveryPhrase(wrongPhrase)

        assertIs<OmniResult.Failure>(result)
        assertFalse(keyManager.isUnlocked())
    }

    @Test
    fun `every operation fails with NotUnlocked before setup`() {
        val keyManager = KeyManager()

        val result = keyManager.encryptForDomain("task", "data".encodeToByteArray())

        assertIs<OmniResult.Failure>(result)
        assertEquals(SecurityError.NotUnlocked, result.error)
    }

    @Test
    fun `encryptForDomain then decryptForDomain round-trips`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("pw".toCharArray())

        val encrypted = keyManager.encryptForDomain("task", "Chiamare il commercialista".encodeToByteArray())
        assertIs<OmniResult.Success<DomainCiphertext>>(encrypted)

        val decrypted = keyManager.decryptForDomain("task", encrypted.value)
        assertIs<OmniResult.Success<ByteArray>>(decrypted)
        assertEquals("Chiamare il commercialista", decrypted.value.decodeToString())
    }

    @Test
    fun `data keys are isolated per domain`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("pw".toCharArray())

        val taskCiphertext = keyManager.encryptForDomain("task", "task secret".encodeToByteArray())
        assertIs<OmniResult.Success<DomainCiphertext>>(taskCiphertext)

        val crossDomainDecrypt = keyManager.decryptForDomain("finance", taskCiphertext.value)
        assertIs<OmniResult.Failure>(crossDomainDecrypt)
    }

    @Test
    fun `rotateDataKey keeps older ciphertext versions decryptable (envelope encryption, TDR-23)`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("pw".toCharArray())

        val v0Ciphertext = keyManager.encryptForDomain("task", "v0 data".encodeToByteArray())
        assertIs<OmniResult.Success<DomainCiphertext>>(v0Ciphertext)
        assertEquals(0, v0Ciphertext.value.keyVersion)

        val rotation = keyManager.rotateDataKey("task")
        assertIs<OmniResult.Success<Int>>(rotation)
        assertEquals(1, rotation.value)

        val v1Ciphertext = keyManager.encryptForDomain("task", "v1 data".encodeToByteArray())
        assertIs<OmniResult.Success<DomainCiphertext>>(v1Ciphertext)
        assertEquals(1, v1Ciphertext.value.keyVersion)

        val v0Decrypted = keyManager.decryptForDomain("task", v0Ciphertext.value)
        assertIs<OmniResult.Success<ByteArray>>(v0Decrypted)
        assertEquals("v0 data", v0Decrypted.value.decodeToString())

        val v1Decrypted = keyManager.decryptForDomain("task", v1Ciphertext.value)
        assertIs<OmniResult.Success<ByteArray>>(v1Decrypted)
        assertEquals("v1 data", v1Decrypted.value.decodeToString())
    }

    @Test
    fun `changePassphrase re-wraps the KEK without touching domain data`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("old passphrase".toCharArray())
        val ciphertext = keyManager.encryptForDomain("task", "unchanged data".encodeToByteArray())
        assertIs<OmniResult.Success<DomainCiphertext>>(ciphertext)

        val changeResult = keyManager.changePassphrase("old passphrase".toCharArray(), "new passphrase".toCharArray())
        assertIs<OmniResult.Success<Unit>>(changeResult)

        keyManager.lock()
        val unlockWithNew = keyManager.unlockWithPassphrase("new passphrase".toCharArray())
        assertIs<OmniResult.Success<Unit>>(unlockWithNew)

        val decrypted = keyManager.decryptForDomain("task", ciphertext.value)
        assertIs<OmniResult.Success<ByteArray>>(decrypted)
        assertEquals("unchanged data", decrypted.value.decodeToString())
    }

    @Test
    fun `changePassphrase fails and does not rotate when the old passphrase is wrong`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("old passphrase".toCharArray())

        val result = keyManager.changePassphrase("wrong old passphrase".toCharArray(), "new passphrase".toCharArray())

        assertIs<OmniResult.Failure>(result)
        keyManager.lock()
        val unlockWithOld = keyManager.unlockWithPassphrase("old passphrase".toCharArray())
        assertIs<OmniResult.Success<Unit>>(unlockWithOld)
    }
}
