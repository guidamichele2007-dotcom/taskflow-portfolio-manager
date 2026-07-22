package com.omnilife.core.security

import com.omnilife.core.common.OmniResult
import com.omnilife.core.security.crypto.AesGcm
import com.omnilife.core.security.crypto.EncryptedBlob
import com.omnilife.core.security.crypto.PlatformCrypto

/**
 * Generic crypto primitives (TDR-23: AES-256-GCM, PBKDF2WithHmacSHA256) —
 * a stateless toolkit, never the key hierarchy itself (that's
 * [KeyManager]'s job). Public because encrypting an arbitrary blob with a
 * caller-supplied key is a legitimate reusable operation (e.g.
 * [FieldCipher]); the sacred key material in [KeyManager] never crosses
 * this boundary as a value.
 */
public interface CryptoService {
    /** A fresh random 256-bit symmetric key. */
    public fun generateKey(): ByteArray

    public fun encrypt(
        key: ByteArray,
        plaintext: ByteArray,
    ): EncryptedBlob

    /** [SecurityError.DecryptionFailed] on a tampered blob or wrong key — never a raw platform exception. */
    public fun decrypt(
        key: ByteArray,
        blob: EncryptedBlob,
    ): OmniResult<ByteArray>

    public fun randomSalt(sizeBytes: Int = DEFAULT_SALT_SIZE_BYTES): ByteArray

    public fun deriveKeyFromPassphrase(
        passphrase: CharArray,
        salt: ByteArray,
        iterations: Int = DEFAULT_PBKDF2_ITERATIONS,
    ): ByteArray

    public companion object {
        /** OWASP 2023 minimum for PBKDF2-HMAC-SHA256 (TDR-23). */
        public const val DEFAULT_PBKDF2_ITERATIONS: Int = 600_000
        public const val DEFAULT_SALT_SIZE_BYTES: Int = 16
        public const val KEY_SIZE_BITS: Int = 256
    }
}

public class RealCryptoService : CryptoService {
    override fun generateKey(): ByteArray = PlatformCrypto.secureRandomBytes(CryptoService.KEY_SIZE_BITS / 8)

    override fun encrypt(
        key: ByteArray,
        plaintext: ByteArray,
    ): EncryptedBlob = AesGcm.encrypt(key, plaintext)

    override fun decrypt(
        key: ByteArray,
        blob: EncryptedBlob,
    ): OmniResult<ByteArray> =
        try {
            OmniResult.Success(AesGcm.decrypt(key, blob))
        } catch (_: Exception) {
            OmniResult.Failure(SecurityError.DecryptionFailed)
        }

    override fun randomSalt(sizeBytes: Int): ByteArray = PlatformCrypto.secureRandomBytes(sizeBytes)

    override fun deriveKeyFromPassphrase(
        passphrase: CharArray,
        salt: ByteArray,
        iterations: Int,
    ): ByteArray = PlatformCrypto.pbkdf2Sha256(passphrase, salt, iterations, CryptoService.KEY_SIZE_BITS)
}
