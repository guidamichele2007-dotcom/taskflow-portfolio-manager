package com.omnilife.domain.task.persistence

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific SQLite driver factory (TDR-20). Only the JVM `actual`
 * is exercised in this sandbox (no Android SDK, no macOS/Xcode — the same
 * gating documented for every other platform target in README-BUILD.md §4).
 */
public expect class DatabaseDriverFactory {
    public fun createDriver(): SqlDriver
}
