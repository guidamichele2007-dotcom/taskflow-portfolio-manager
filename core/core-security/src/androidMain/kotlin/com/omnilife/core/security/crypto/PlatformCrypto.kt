package com.omnilife.core.security.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Android actual (TDR-23): identical JCE surface to the JVM actual — Android
 * has supported `javax.crypto` AES/GCM and PBKDF2WithHmacSHA256 since API 26
 * (this project's `minSdk`, see `omnilife.kmp.module`). Written for real but
 * **not compiled/verified in this sandbox** (no Android SDK — same gating as
 * every other `androidMain` source in this repo since Sprint 1).
 *
 * A production build should prefer Android Keystore-backed keys
 * (`AndroidKeyStore` provider, hardware-backed on supported devices) over
 * raw `SecretKeySpec` for the top of the key hierarchy (the KEK) — that
 * upgrade is a documented Sprint 4 follow-up (see sprint3_report.md), not
 * done here because it cannot be exercised without a device/emulator.
 */
internal actual object PlatformCrypto {
    private const val GCM_TAG_LENGTH_BITS = 128
    private val secureRandom = SecureRandom()

    actual fun secureRandomBytes(size: Int): ByteArray = ByteArray(size).also(secureRandom::nextBytes)

    actual fun aesGcmEncrypt(
        key: ByteArray,
        nonce: ByteArray,
        plaintext: ByteArray,
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce))
        return cipher.doFinal(plaintext)
    }

    actual fun aesGcmDecrypt(
        key: ByteArray,
        nonce: ByteArray,
        ciphertext: ByteArray,
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce))
        return cipher.doFinal(ciphertext)
    }

    actual fun pbkdf2Sha256(
        password: CharArray,
        salt: ByteArray,
        iterations: Int,
        keyLengthBits: Int,
    ): ByteArray {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password, salt, iterations, keyLengthBits)
        return factory.generateSecret(spec).encoded
    }
}
