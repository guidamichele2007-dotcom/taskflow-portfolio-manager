package com.omnilife.core.security.crypto

/**
 * An AES-256-GCM ciphertext (TDR-23) and the nonce used to produce it — safe
 * to expose publicly and to persist/transmit: it carries no key material.
 * [ciphertext] includes the GCM authentication tag (standard JCE behavior).
 */
public class EncryptedBlob(public val nonce: ByteArray, public val ciphertext: ByteArray) {
    override fun equals(other: Any?): Boolean =
        other is EncryptedBlob && nonce.contentEquals(other.nonce) && ciphertext.contentEquals(other.ciphertext)

    override fun hashCode(): Int = 31 * nonce.contentHashCode() + ciphertext.contentHashCode()

    /** `nonce (fixed 12 bytes) + ciphertext` — a flat wire/storage format, safe to persist (no key material). */
    public fun serialize(): ByteArray = nonce + ciphertext

    public companion object {
        private const val NONCE_SIZE_BYTES = 12

        public fun deserialize(bytes: ByteArray): EncryptedBlob {
            require(bytes.size > NONCE_SIZE_BYTES) {
                "EncryptedBlob.deserialize: input too short (${bytes.size} bytes)"
            }
            return EncryptedBlob(
                nonce = bytes.copyOfRange(0, NONCE_SIZE_BYTES),
                ciphertext = bytes.copyOfRange(NONCE_SIZE_BYTES, bytes.size),
            )
        }
    }
}

/**
 * AES-256-GCM with a fresh random nonce per call (internal — see
 * [PlatformCrypto] for why this stays out of the public API).
 */
internal object AesGcm {
    private const val NONCE_SIZE_BYTES = 12

    fun encrypt(
        key: ByteArray,
        plaintext: ByteArray,
    ): EncryptedBlob {
        val nonce = PlatformCrypto.secureRandomBytes(NONCE_SIZE_BYTES)
        return EncryptedBlob(nonce, PlatformCrypto.aesGcmEncrypt(key, nonce, plaintext))
    }

    fun decrypt(
        key: ByteArray,
        blob: EncryptedBlob,
    ): ByteArray = PlatformCrypto.aesGcmDecrypt(key, blob.nonce, blob.ciphertext)
}
