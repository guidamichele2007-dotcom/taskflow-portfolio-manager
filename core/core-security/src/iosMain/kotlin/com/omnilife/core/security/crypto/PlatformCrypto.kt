package com.omnilife.core.security.crypto

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecSuccess
import platform.Security.kSecRandomDefault

/**
 * iOS actual (TDR-23) — **honestly incomplete**, not a working
 * implementation, and this repository cannot compile or run it (no macOS/
 * Xcode host, same environmental gate as every Apple target since Sprint
 * 1). [secureRandomBytes] delegates to `SecRandomCopyBytes`, a
 * well-documented, stable Security.framework API that Kotlin/Native's
 * bundled `platform.Security` interop exposes directly, and is written with
 * real confidence.
 *
 * [aesGcmEncrypt]/[aesGcmDecrypt]/[pbkdf2Sha256] are **not implemented**:
 * Apple's AES-GCM/PBKDF2 primitives live in CryptoKit (Swift-only) or
 * CommonCrypto's less ergonomic C API (`CCCryptorGCM`), and getting the
 * cinterop signature exactly right is not something this session can
 * verify by compiling or running it — shipping a plausible-looking but
 * unverified cinterop call for an AEAD cipher is worse than failing loudly.
 * Documented as a Sprint 4 blocker in sprint3_report.md: wire a small
 * Swift/CryptoKit bridging shim (the standard approach for KMP + CryptoKit)
 * and replace these three functions.
 */
@OptIn(ExperimentalForeignApi::class)
internal actual object PlatformCrypto {
    actual fun secureRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        val status =
            bytes.usePinned { pinned ->
                SecRandomCopyBytes(kSecRandomDefault, size.toULong(), pinned.addressOf(0))
            }
        check(status == errSecSuccess) { "SecRandomCopyBytes failed with status $status" }
        return bytes
    }

    actual fun aesGcmEncrypt(
        key: ByteArray,
        nonce: ByteArray,
        plaintext: ByteArray,
    ): ByteArray = throw NotImplementedError("iOS AES-GCM pending a CryptoKit bridging shim — see TDR-23, Sprint 4 blocker")

    actual fun aesGcmDecrypt(
        key: ByteArray,
        nonce: ByteArray,
        ciphertext: ByteArray,
    ): ByteArray = throw NotImplementedError("iOS AES-GCM pending a CryptoKit bridging shim — see TDR-23, Sprint 4 blocker")

    actual fun pbkdf2Sha256(
        password: CharArray,
        salt: ByteArray,
        iterations: Int,
        keyLengthBits: Int,
    ): ByteArray = throw NotImplementedError("iOS PBKDF2 pending a CommonCrypto/CryptoKit bridging shim — see TDR-23, Sprint 4 blocker")
}
