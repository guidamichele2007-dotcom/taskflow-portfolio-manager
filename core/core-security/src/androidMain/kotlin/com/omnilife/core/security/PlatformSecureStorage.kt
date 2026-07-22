package com.omnilife.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Android actual (TDR-23): backed by `androidx.security.crypto`'s
 * `EncryptedSharedPreferences`, itself backed by an Android Keystore
 * master key (hardware-backed on supported devices) — the real OS-level
 * secure storage the Security & Privacy Bible describes. Written for real
 * but **not compiled/verified in this sandbox** (no Android SDK).
 *
 * `expect fun platformSecureStorage(): SecureStorage` takes no parameters,
 * but Android's real secure storage needs a `Context`. [initialize] must be
 * called once from the Android application entry point before
 * [platformSecureStorage] is used — the standard applicationContext-
 * singleton pattern for exactly this kind of platform bridge.
 */
public object AndroidSecureStorageInit {
    private var appContext: Context? = null

    public fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    internal fun requireContext(): Context =
        checkNotNull(appContext) { "AndroidSecureStorageInit.initialize(context) must run before platformSecureStorage()" }
}

public actual fun platformSecureStorage(): SecureStorage = AndroidEncryptedPreferencesSecureStorage(AndroidSecureStorageInit.requireContext())

internal class AndroidEncryptedPreferencesSecureStorage(context: Context) : SecureStorage {
    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val prefs =
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    override fun putBytes(
        key: String,
        value: ByteArray,
    ) {
        prefs.edit().putString(key, value.toBase64()).apply()
    }

    override fun getBytes(key: String): ByteArray? = prefs.getString(key, null)?.fromBase64()

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_FILE_NAME = "omnilife_secure_storage"
    }
}

private fun ByteArray.toBase64(): String = android.util.Base64.encodeToString(this, android.util.Base64.NO_WRAP)

private fun String.fromBase64(): ByteArray = android.util.Base64.decode(this, android.util.Base64.NO_WRAP)
