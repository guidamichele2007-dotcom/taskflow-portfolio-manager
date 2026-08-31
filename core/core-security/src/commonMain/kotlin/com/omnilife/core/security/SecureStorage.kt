package com.omnilife.core.security

/**
 * Platform-backed secret storage — where [KeyManager] persists wrapped
 * keys (never unwrapped ones) between app launches. Values are opaque
 * bytes; this interface has no notion of what they contain.
 */
public interface SecureStorage {
    public fun putBytes(
        key: String,
        value: ByteArray,
    )

    public fun getBytes(key: String): ByteArray?

    public fun remove(key: String)

    public fun clear()
}

/**
 * In-memory [SecureStorage] — the real, always-available implementation on
 * every KMP target, useful for tests and as the explicit fallback this
 * module ships everywhere. It is **not** the OS-backed secure storage the
 * Security & Privacy Bible describes (iOS Keychain / Android Keystore-
 * backed `EncryptedSharedPreferences`) — those are platform actuals to be
 * added when a real device/emulator is available to verify them (see
 * sprint3_report.md).
 */
public class InMemorySecureStorage : SecureStorage {
    private val entries = mutableMapOf<String, ByteArray>()

    override fun putBytes(
        key: String,
        value: ByteArray,
    ) {
        entries[key] = value
    }

    override fun getBytes(key: String): ByteArray? = entries[key]

    override fun remove(key: String) {
        entries.remove(key)
    }

    override fun clear() {
        entries.clear()
    }
}

/**
 * The best [SecureStorage] this platform can offer (TDR-23). JVM returns
 * [InMemorySecureStorage] (there is no OS-level secure storage on plain
 * JVM/Desktop — this is the honest, always-available option). Android/iOS
 * actuals wrap the real platform secret stores and are written for real but
 * unverified in this sandbox (no SDK/host — see sprint3_report.md).
 */
public expect fun platformSecureStorage(): SecureStorage
