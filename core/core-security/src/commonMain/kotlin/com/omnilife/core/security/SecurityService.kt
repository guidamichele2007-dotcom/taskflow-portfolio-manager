package com.omnilife.core.security

/**
 * Facade over this module's six components (Functional Bible SEC-001…003,
 * MFC §5; Technical Architecture Bible §10; TDR-04/23) — a single
 * composition point a future app entry point wires up once, rather than
 * constructing [KeyManager]/[AppLockService]/[RecoveryKeyService]
 * separately everywhere. Each component also works standalone (most tests
 * in this module exercise them directly), matching how [KeyManager] and
 * [FieldCipher] are meant to be used independently by future `domain-*`
 * repositories.
 */
public class SecurityService(
    public val keyManager: KeyManager = KeyManager(),
    public val appLock: AppLockService = AppLockService(),
    public val recoveryKeyService: RecoveryKeyService = RecoveryKeyService(),
) {
    public fun fieldCipherFor(domain: String): FieldCipher = FieldCipher(keyManager, domain)
}
