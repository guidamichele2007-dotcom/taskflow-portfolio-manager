package com.omnilife.core.security.crypto

/**
 * The only platform-specific surface in `core-security` (TDR-23): four
 * primitives, each backed by the platform's own cryptography library
 * (JVM/Android: `javax.crypto`/JCE; iOS: Apple's crypto APIs — see the
 * `iosMain` actual for the honest state of that port). Every other type in
 * this module (`KeyManager`, `AppLockService`, `RecoveryKeyService`, ...) is
 * pure Kotlin built on top of this and fully testable on the JVM target.
 *
 * Deliberately `internal`: no key material or raw cipher primitive crosses
 * this module's public boundary (Technical Architecture Bible §10 §3 —
 * "il Servizio di Sicurezza espone solo operazioni, mai la chiave stessa").
 */
internal expect object PlatformCrypto {
    /** Cryptographically secure random bytes (nonces, salts, key material). */
    fun secureRandomBytes(size: Int): ByteArray

    /** AES-256-GCM encrypt (TDR-23). Returns ciphertext with the GCM authentication tag appended. */
    fun aesGcmEncrypt(
        key: ByteArray,
        nonce: ByteArray,
        plaintext: ByteArray,
    ): ByteArray

    /** AES-256-GCM decrypt; throws if the authentication tag doesn't verify (tamper/wrong key). */
    fun aesGcmDecrypt(
        key: ByteArray,
        nonce: ByteArray,
        ciphertext: ByteArray,
    ): ByteArray

    /** PBKDF2WithHmacSHA256 (TDR-23), 256-bit output by convention of this module's callers. */
    fun pbkdf2Sha256(
        password: CharArray,
        salt: ByteArray,
        iterations: Int,
        keyLengthBits: Int,
    ): ByteArray
}
