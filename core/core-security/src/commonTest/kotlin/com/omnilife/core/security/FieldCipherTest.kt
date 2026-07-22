package com.omnilife.core.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FieldCipherTest {
    @Test
    fun `encryptField then decryptField round-trips through the opaque column string`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("pw".toCharArray())
        val fieldCipher = keyManager.let { FieldCipher(it, "task") }

        val encrypted = fieldCipher.encryptField("Chiamare il commercialista")
        assertIs<OmniFieldCipherResult.Success>(encrypted)

        val decrypted = fieldCipher.decryptField(encrypted.column)
        assertIs<OmniFieldCipherResult.SuccessText>(decrypted)
        assertEquals("Chiamare il commercialista", decrypted.text)
    }

    @Test
    fun `the opaque column string carries no readable plaintext`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("pw".toCharArray())
        val fieldCipher = FieldCipher(keyManager, "task")

        val encrypted = fieldCipher.encryptField("a very secret task title")
        assertIs<OmniFieldCipherResult.Success>(encrypted)

        assertEquals(false, encrypted.column.contains("secret"))
    }

    @Test
    fun `decryptField fails cleanly on a locked KeyManager`() {
        val keyManager = KeyManager()
        keyManager.setUpMasterKey("pw".toCharArray())
        val fieldCipher = FieldCipher(keyManager, "task")
        val encrypted = fieldCipher.encryptField("data")
        assertIs<OmniFieldCipherResult.Success>(encrypted)
        keyManager.lock()

        val decrypted = fieldCipher.decryptField(encrypted.column)

        assertIs<OmniFieldCipherResult.Failure>(decrypted)
    }
}
