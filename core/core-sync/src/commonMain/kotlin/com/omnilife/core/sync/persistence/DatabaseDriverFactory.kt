package com.omnilife.core.sync.persistence

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific SQLite driver factory (TDR-24 addendum). Only the JVM
 * `actual` is exercised in this sandbox (no Android SDK, no macOS/Xcode —
 * the same gating documented for every other platform target since
 * Sprint 1, README-BUILD.md §4).
 */
public expect class DatabaseDriverFactory {
    public fun createDriver(): SqlDriver
}
