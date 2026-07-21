package com.omnilife.domain.task.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS driver. Not compiled/verified in this sandbox (no macOS/Xcode host —
 * see README-BUILD.md §4); standard SQLDelight native driver usage.
 */
public actual class DatabaseDriverFactory {
    public actual fun createDriver(): SqlDriver = NativeSqliteDriver(TaskDatabase.Schema, "omnilife-task.db")
}
