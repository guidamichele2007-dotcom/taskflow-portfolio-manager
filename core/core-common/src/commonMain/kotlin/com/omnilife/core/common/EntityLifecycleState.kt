package com.omnilife.core.common

/**
 * Universal entity lifecycle (Functional Bible MFC §2; Data Model Bible §00
 * `stato_ciclo_vita`). Applies identically to every user entity across every
 * module ("one anatomy", Product Bible P33) — modules define only their own
 * fields, never their own lifecycle.
 *
 * Transitions allowed (MFC §2): ACTIVE <-> (edited), ACTIVE -> ARCHIVED,
 * ARCHIVED -> ACTIVE (restore), ACTIVE|ARCHIVED -> TRASHED, TRASHED -> ACTIVE
 * (restore), TRASHED -> PERMANENTLY_DELETED (after 30 days or explicit purge,
 * MFC-R-10). No other transition exists (INV-05).
 */
public enum class EntityLifecycleState {
    ACTIVE,
    ARCHIVED,
    TRASHED,
    PERMANENTLY_DELETED,
}
