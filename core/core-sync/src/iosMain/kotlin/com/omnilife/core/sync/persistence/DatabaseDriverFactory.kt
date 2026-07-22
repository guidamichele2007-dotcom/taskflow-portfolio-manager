package com.omnilife.core.sync.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/** iOS driver. Not compiled/verified in this sandbox (no macOS/Xcode host — README-BUILD.md §4). */
public actual class DatabaseDriverFactory {
    public actual fun createDriver(): SqlDriver = NativeSqliteDriver(SyncDatabase.Schema, "omnilife-sync.db")
}
