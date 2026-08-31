package com.omnilife.domain.account.persistence

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific SQLite driver factory (TDR-20), same convention as `domain-task`'s. Only the
 * JVM `actual` is exercised in this sandbox (no Android SDK, no macOS/Xcode).
 */
public expect class DatabaseDriverFactory {
    public fun createDriver(): SqlDriver
}
