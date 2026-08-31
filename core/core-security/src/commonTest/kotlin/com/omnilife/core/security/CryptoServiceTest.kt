package com.omnilife.core.security

import com.omnilife.core.common.OmniResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class CryptoServiceTest {
    private val crypto: CryptoService = RealCryptoService()

    @Test
    fun `encrypt then decrypt round-trips the original plaintext`() {
        val key = crypto.generateKey()
        val plaintext = "Chiamare il commercialista".encodeToByteArray()

        val blob = crypto.encrypt(key, plaintext)
        val result = crypto.decrypt(key, blob)

        assertIs<OmniResult.Success<ByteArray>>(result)
        assertEquals(plaintext.toList(), result.value.toList())
    }

    @Test
    fun `decrypting with the wrong key fails instead of returning garbage`() {
        val key = crypto.generateKey()
        val wrongKey = crypto.generateKey()
        val blob = crypto.encrypt(key, "secret".encodeToByteArray())

        val result = crypto.decrypt(wrongKey, blob)

        assertIs<OmniResult.Failure>(result)
        assertEquals(SecurityError.DecryptionFailed, result.error)
    }

    @Test
    fun `two encryptions of the same plaintext produce different ciphertext (fresh nonce each time)`() {
        val key = crypto.generateKey()
        val plaintext = "same text".encodeToByteArray()

        val first = crypto.encrypt(key, plaintext)
        val second = crypto.encrypt(key, plaintext)

        assertNotEquals(first, second)
    }

    @Test
    fun `deriveKeyFromPassphrase is deterministic for the same passphrase and salt`() {
        val salt = crypto.randomSalt()
        val key1 = crypto.deriveKeyFromPassphrase("correct horse battery staple".toCharArray(), salt, iterations = 1000)
        val key2 = crypto.deriveKeyFromPassphrase("correct horse battery staple".toCharArray(), salt, iterations = 1000)

        assertEquals(key1.toList(), key2.toList())
    }

    @Test
    fun `deriveKeyFromPassphrase differs for a different salt`() {
        val passphrase = "same passphrase".toCharArray()
        val key1 = crypto.deriveKeyFromPassphrase(passphrase, crypto.randomSalt(), iterations = 1000)
        val key2 = crypto.deriveKeyFromPassphrase(passphrase, crypto.randomSalt(), iterations = 1000)

        assertNotEquals(key1.toList(), key2.toList())
    }

    @Test
    fun `generateKey produces 256-bit keys`() {
        assertEquals(CryptoService.KEY_SIZE_BITS / 8, crypto.generateKey().size)
    }
}
