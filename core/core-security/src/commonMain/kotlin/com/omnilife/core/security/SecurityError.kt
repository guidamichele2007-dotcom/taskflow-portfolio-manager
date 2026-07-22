package com.omnilife.core.security

import com.omnilife.core.common.DomainError

/**
 * `core-security` errors (TDR-21 convention) — the vocabulary its
 * operations return in [com.omnilife.core.common.OmniResult.Failure].
 */
public sealed class SecurityError(override val message: String) : DomainError {
    /** Wrong passphrase, or no master key has been set up yet. */
    public data object InvalidPassphrase : SecurityError("Passphrase non valida")

    /** SEC-002: the recovery phrase's words don't match what was generated. */
    public data object InvalidRecoveryPhrase : SecurityError("Chiave di recupero non valida")

    /** A domain (module) requested a data key version that doesn't exist — see `KeyManager.encryptForDomain`. */
    public data class DataKeyNotFound(val domain: String) :
        SecurityError("Nessuna chiave dati per il dominio '$domain'")

    /** The key hierarchy hasn't been unlocked in this session (app-locked or not yet set up). */
    public data object NotUnlocked : SecurityError("Il Servizio di Sicurezza non è sbloccato")

    /** AES-GCM authentication failed — tampered ciphertext or wrong key (never silently swallowed). */
    public data object DecryptionFailed : SecurityError("Decifratura fallita: dato manomesso o chiave errata")
}
