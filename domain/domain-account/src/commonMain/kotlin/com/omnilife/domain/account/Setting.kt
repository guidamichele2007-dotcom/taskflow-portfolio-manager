package com.omnilife.domain.account

import kotlinx.datetime.Instant

/**
 * DM-SYS-06 `ambito`: whether a [Setting] follows the account across devices, or stays local to
 * this one (SET-R-03 — biometric lock/timeout and backup-over-network are per-device; everything
 * else syncs).
 */
public enum class SettingScope {
    SYNCED_ACROSS_DEVICES,
    DEVICE_LOCAL,
}

/**
 * SET §2's closed catalog (SET-R-01), restricted to the entries this sprint's Vertical Slice
 * genuinely wires up end to end: Aspetto (tema, colore accento) and Notifiche (budget
 * giornaliero, orari silenzio). Every other catalog group (Account, Sicurezza, Dati, Privacy,
 * Abbonamento, Lingua/formati, Accessibilità, Aiuto) stays out of this sprint's scope — adding a
 * key here without one of those groups' real backend behind it would be exactly the "impostazione
 * senza funzione reale" this catalog is designed to prevent.
 */
public enum class SettingKey(public val scope: SettingScope) {
    THEME(SettingScope.SYNCED_ACROSS_DEVICES),
    ACCENT_COLOR(SettingScope.SYNCED_ACROSS_DEVICES),
    NOTIFICATION_DAILY_BUDGET(SettingScope.SYNCED_ACROSS_DEVICES),
    NOTIFICATION_QUIET_HOURS_START(SettingScope.SYNCED_ACROSS_DEVICES),
    NOTIFICATION_QUIET_HOURS_END(SettingScope.SYNCED_ACROSS_DEVICES),
}

/** SET §2 "Aspetto → tema (sistema/chiaro/scuro)" — [SettingKey.THEME]'s value domain. */
public enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

/**
 * SET §2 "Aspetto → colore accento (dal set)". The concrete palette is a `core-designtokens`
 * concern (this module has no UI dependency) — these names deliberately mirror
 * `core-designtokens`'s `OmniAccent` enum exactly (BLU/VERDE/VIOLA/CORALLO/PETROLIO/INDACO), so
 * the composition root can map one to the other by name alone, with no cross-module type
 * dependency between `domain-account` and `core-designtokens`.
 */
public enum class AccentColor {
    BLU,
    VERDE,
    VIOLA,
    CORALLO,
    PETROLIO,
    INDACO,
}

/**
 * DM-SYS-06 · Setting — one entry of the closed catalog. [key] doubles as the entity's identity:
 * SET §2 is a fixed enumeration, not a free-form key-value store, so there is at most one
 * [Setting] per [SettingKey] (per account/device, per [SettingScope]).
 */
public data class Setting(
    val key: SettingKey,
    val value: String,
    val scope: SettingScope = key.scope,
    val modifiedAt: Instant,
)

/** SET §2's stated defaults, applied when no [Setting] row exists yet for a key (first run). */
public object SettingDefaults {
    public const val THEME: String = "SYSTEM"
    public const val ACCENT_COLOR: String = "INDACO"

    /** "budget giornaliero (0–10)... 3" */
    public const val NOTIFICATION_DAILY_BUDGET: String = "3"

    /** "orari silenzio... 22–8" */
    public const val NOTIFICATION_QUIET_HOURS_START: String = "22:00"
    public const val NOTIFICATION_QUIET_HOURS_END: String = "08:00"

    public fun forKey(key: SettingKey): String =
        when (key) {
            SettingKey.THEME -> THEME
            SettingKey.ACCENT_COLOR -> ACCENT_COLOR
            SettingKey.NOTIFICATION_DAILY_BUDGET -> NOTIFICATION_DAILY_BUDGET
            SettingKey.NOTIFICATION_QUIET_HOURS_START -> NOTIFICATION_QUIET_HOURS_START
            SettingKey.NOTIFICATION_QUIET_HOURS_END -> NOTIFICATION_QUIET_HOURS_END
        }
}
