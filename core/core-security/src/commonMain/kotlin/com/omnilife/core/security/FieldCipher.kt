package com.omnilife.core.security

import com.omnilife.core.common.DomainError
import com.omnilife.core.common.OmniResult
import com.omnilife.core.security.crypto.Base64
import com.omnilife.core.security.crypto.EncryptedBlob

/**
 * Application-level "encrypted database" building block (TDR-23): encrypts
 * one text field/blob at a time under a given domain's data key, producing
 * a value safe to store as an opaque column in an otherwise-unencrypted
 * SQLite database (the persistence layer this repo already has since
 * Sprint 1, TDR-20). This is **not** true page-level encryption (SQLCipher,
 * TDR-06) — that requires a native library this sandbox cannot link or
 * verify (see TDR-23's addendum). `FieldCipher` is the real, working,
 * testable alternative available today; wiring it into an existing
 * `domain-*` repository is explicitly out of this sprint's scope (only the
 * four Core Platform modules) and is tracked as a Sprint 4 item.
 */
public class FieldCipher(private val keyManager: KeyManager, private val domain: String) {
    /** Encrypts [plaintext] and returns a value safe to store as a single opaque column. */
    public fun encryptField(plaintext: String): OmniFieldCipherResult =
        when (val ciphertext = keyManager.encryptForDomain(domain, plaintext.encodeToByteArray())) {
            is OmniResult.Success -> OmniFieldCipherResult.Success(encodeColumn(ciphertext.value))
            is OmniResult.Failure -> OmniFieldCipherResult.Failure(ciphertext.error)
        }

    public fun decryptField(column: String): OmniFieldCipherResult =
        when (val result = keyManager.decryptForDomain(domain, decodeColumn(column))) {
            is OmniResult.Success -> OmniFieldCipherResult.SuccessText(result.value.decodeToString())
            is OmniResult.Failure -> OmniFieldCipherResult.Failure(result.error)
        }

    private fun encodeColumn(ciphertext: DomainCiphertext): String =
        "${ciphertext.keyVersion}:${Base64.encode(ciphertext.blob.serialize())}"

    private fun decodeColumn(column: String): DomainCiphertext {
        val separatorIndex = column.indexOf(':')
        require(separatorIndex > 0) { "FieldCipher column value missing key-version prefix" }
        val keyVersion = column.substring(0, separatorIndex).toInt()
        val blob = EncryptedBlob.deserialize(Base64.decode(column.substring(separatorIndex + 1)))
        return DomainCiphertext(keyVersion, blob)
    }
}

/**
 * Distinct success shapes for text (decrypt) vs opaque column (encrypt) —
 * keeps the caller from mixing them up by type.
 */
public sealed class OmniFieldCipherResult {
    public data class Success(public val column: String) : OmniFieldCipherResult()

    public data class SuccessText(public val text: String) : OmniFieldCipherResult()

    public data class Failure(public val error: DomainError) : OmniFieldCipherResult()
}
