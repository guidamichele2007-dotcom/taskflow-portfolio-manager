package com.omnilife.core.security

import com.omnilife.core.common.OmniResult
import com.omnilife.core.security.crypto.EncryptedBlob

/**
 * A domain (module)'s ciphertext, tagged with which [KeyManager] data-key
 * version produced it (envelope encryption — TDR-23).
 */
public class DomainCiphertext(public val keyVersion: Int, public val blob: EncryptedBlob)

/**
 * The key hierarchy (Technical Architecture Bible §10 §3, 06-sicurezza-e-privacy.md
 * §2.1): passphrase → Master Key (PBKDF2) → Key Encryption Key (KEK) →
 * one Data Encryption Key per domain/module. **Never exposes a raw key as a
 * value** — every operation is "encrypt this blob"/"decrypt this blob"
 * (Interface Segregation applied to security, per the Bible). Only wrapped
 * (encrypted) key material is persisted, via [SecureStorage] — DEKs
 * themselves are wrapped by the KEK before they ever reach storage, so
 * [lock] can safely drop every unwrapped key from memory and still recover
 * them on the next successful unlock (envelope encryption end to end, not
 * just for the KEK).
 */
public class KeyManager(
    private val crypto: CryptoService = RealCryptoService(),
    private val storage: SecureStorage = InMemorySecureStorage(),
) {
    private var kek: ByteArray? = null
    private val dataKeysByDomain = mutableMapOf<String, MutableList<ByteArray>>()

    public fun isUnlocked(): Boolean = kek != null

    /**
     * First-time setup (SEC-002): generates the KEK and a recovery phrase,
     * wraps the KEK both ways, persists the wraps.
     */
    public fun setUpMasterKey(
        passphrase: CharArray,
        recoveryKeyService: RecoveryKeyService = RecoveryKeyService(),
    ): RecoveryPhrase {
        val salt = crypto.randomSalt()
        val masterKey = crypto.deriveKeyFromPassphrase(passphrase, salt)
        val newKek = crypto.generateKey()

        storage.putBytes(KEY_PASSPHRASE_SALT, salt)
        storage.putBytes(KEY_WRAPPED_KEK_BY_PASSPHRASE, crypto.encrypt(masterKey, newKek).serialize())

        val recoveryPhrase = recoveryKeyService.generate()
        val recoveryKeyBytes = recoveryKeyService.toKeyMaterial(recoveryPhrase)
        storage.putBytes(KEY_WRAPPED_KEK_BY_RECOVERY, crypto.encrypt(recoveryKeyBytes, newKek).serialize())

        kek = newKek
        return recoveryPhrase
    }

    public fun unlockWithPassphrase(passphrase: CharArray): OmniResult<Unit> {
        val invalidPassphrase = OmniResult.Failure(SecurityError.InvalidPassphrase)
        val salt = storage.getBytes(KEY_PASSPHRASE_SALT) ?: return invalidPassphrase
        val wrappedBytes = storage.getBytes(KEY_WRAPPED_KEK_BY_PASSPHRASE) ?: return invalidPassphrase
        val masterKey = crypto.deriveKeyFromPassphrase(passphrase, salt)
        return when (val unwrapped = crypto.decrypt(masterKey, EncryptedBlob.deserialize(wrappedBytes))) {
            is OmniResult.Success -> {
                unlockedWith(unwrapped.value)
                OmniResult.Success(Unit)
            }
            is OmniResult.Failure -> OmniResult.Failure(SecurityError.InvalidPassphrase)
        }
    }

    public fun unlockWithRecoveryPhrase(
        phrase: RecoveryPhrase,
        recoveryKeyService: RecoveryKeyService = RecoveryKeyService(),
    ): OmniResult<Unit> {
        val invalidPhrase = OmniResult.Failure(SecurityError.InvalidRecoveryPhrase)
        val wrappedBytes = storage.getBytes(KEY_WRAPPED_KEK_BY_RECOVERY) ?: return invalidPhrase
        val recoveryKeyBytes = recoveryKeyService.toKeyMaterial(phrase)
        return when (val unwrapped = crypto.decrypt(recoveryKeyBytes, EncryptedBlob.deserialize(wrappedBytes))) {
            is OmniResult.Success -> {
                unlockedWith(unwrapped.value)
                OmniResult.Success(Unit)
            }
            is OmniResult.Failure -> OmniResult.Failure(SecurityError.InvalidRecoveryPhrase)
        }
    }

    /**
     * Re-wraps the KEK under a new passphrase — never touches domain data
     * (TDR-23/06-sicurezza-e-privacy.md §2.1); DEKs are wrapped by the KEK
     * itself, not the passphrase, so they need no re-wrap here.
     */
    public fun changePassphrase(
        oldPassphrase: CharArray,
        newPassphrase: CharArray,
    ): OmniResult<Unit> {
        val currentKek = kek ?: return OmniResult.Failure(SecurityError.NotUnlocked)
        val verify = unlockWithPassphrase(oldPassphrase)
        if (verify is OmniResult.Failure) return verify
        val newSalt = crypto.randomSalt()
        val newMasterKey = crypto.deriveKeyFromPassphrase(newPassphrase, newSalt)
        storage.putBytes(KEY_PASSPHRASE_SALT, newSalt)
        storage.putBytes(KEY_WRAPPED_KEK_BY_PASSPHRASE, crypto.encrypt(newMasterKey, currentKek).serialize())
        return OmniResult.Success(Unit)
    }

    /** Drops every unwrapped key from memory (KEK and every domain's DEKs) — all recoverable on the next unlock. */
    public fun lock() {
        kek = null
        dataKeysByDomain.clear()
    }

    /** Generates a new Data Encryption Key for [domain], keeping prior versions decryptable (envelope encryption). */
    public fun rotateDataKey(domain: String): OmniResult<Int> {
        val currentKek = kek ?: return OmniResult.Failure(SecurityError.NotUnlocked)
        val versions = loadedVersions(domain, currentKek)
        val newVersionIndex = versions.size
        val newDek = crypto.generateKey()
        versions.add(newDek)
        persistDataKey(domain, newVersionIndex, newDek, currentKek)
        return OmniResult.Success(newVersionIndex)
    }

    public fun encryptForDomain(
        domain: String,
        plaintext: ByteArray,
    ): OmniResult<DomainCiphertext> {
        val currentKek = kek ?: return OmniResult.Failure(SecurityError.NotUnlocked)
        val versions = loadedVersions(domain, currentKek)
        if (versions.isEmpty()) {
            val firstDek = crypto.generateKey()
            versions.add(firstDek)
            persistDataKey(domain, 0, firstDek, currentKek)
        }
        val latestVersion = versions.lastIndex
        return OmniResult.Success(DomainCiphertext(latestVersion, crypto.encrypt(versions[latestVersion], plaintext)))
    }

    public fun decryptForDomain(
        domain: String,
        ciphertext: DomainCiphertext,
    ): OmniResult<ByteArray> {
        val currentKek = kek ?: return OmniResult.Failure(SecurityError.NotUnlocked)
        val keyNotFound = OmniResult.Failure(SecurityError.DataKeyNotFound(domain))
        val versions = loadedVersions(domain, currentKek)
        val key = versions.getOrNull(ciphertext.keyVersion) ?: return keyNotFound
        return crypto.decrypt(key, ciphertext.blob)
    }

    private fun unlockedWith(unwrappedKek: ByteArray) {
        kek = unwrappedKek
        dataKeysByDomain.clear()
    }

    /** In-memory cache, transparently rehydrated from [storage] (unwrapped with [currentKek]) after a fresh unlock. */
    private fun loadedVersions(
        domain: String,
        currentKek: ByteArray,
    ): MutableList<ByteArray> =
        dataKeysByDomain.getOrPut(domain) {
            val count = storage.getBytes(dekCountKey(domain))?.decodeToString()?.toIntOrNull() ?: 0
            (0 until count)
                .mapNotNullTo(mutableListOf()) { version ->
                    storage.getBytes(dekKey(domain, version))
                        ?.let { EncryptedBlob.deserialize(it) }
                        ?.let { (crypto.decrypt(currentKek, it) as? OmniResult.Success)?.value }
                }
        }

    private fun persistDataKey(
        domain: String,
        version: Int,
        dek: ByteArray,
        currentKek: ByteArray,
    ) {
        storage.putBytes(dekKey(domain, version), crypto.encrypt(currentKek, dek).serialize())
        storage.putBytes(dekCountKey(domain), (version + 1).toString().encodeToByteArray())
    }

    private fun dekKey(
        domain: String,
        version: Int,
    ) = "km.dek.$domain.$version"

    private fun dekCountKey(domain: String) = "km.dek.$domain.count"

    private companion object {
        const val KEY_PASSPHRASE_SALT = "km.passphrase_salt"
        const val KEY_WRAPPED_KEK_BY_PASSPHRASE = "km.wrapped_kek.passphrase"
        const val KEY_WRAPPED_KEK_BY_RECOVERY = "km.wrapped_kek.recovery"
    }
}
