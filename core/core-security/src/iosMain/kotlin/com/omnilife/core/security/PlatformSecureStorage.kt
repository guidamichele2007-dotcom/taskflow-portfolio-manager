package com.omnilife.core.security

/**
 * iOS actual (TDR-23) — **honestly incomplete**, same status as
 * [com.omnilife.core.security.crypto.PlatformCrypto]'s iOS actual: this
 * repository has no macOS/Xcode host to compile or run Apple targets
 * against (same environmental gate since Sprint 1), so this file is never
 * built here regardless of what it contains — `iosArm64()`/
 * `iosSimulatorArm64()`/`iosX64()` are only registered by the
 * `omnilife.kmp.module` convention plugin on a macOS host.
 *
 * The intended design (not implemented, to avoid shipping cinterop code
 * this session cannot verify even compiles): back [SecureStorage] with the
 * Keychain's `kSecClassGenericPassword` item class via `SecItemAdd`/
 * `SecItemCopyMatching`/`SecItemUpdate`/`SecItemDelete`, keyed by
 * `kSecAttrService`/`kSecAttrAccount`. Tracked as the same Sprint 4 blocker
 * as the AES-GCM port — see sprint3_report.md.
 */
public actual fun platformSecureStorage(): SecureStorage =
    throw NotImplementedError("iOS Keychain-backed SecureStorage pending verification on a real host — see TDR-23, Sprint 4 blocker")
