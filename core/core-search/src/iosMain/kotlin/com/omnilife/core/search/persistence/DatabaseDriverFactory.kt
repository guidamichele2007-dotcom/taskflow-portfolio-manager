package com.omnilife.core.search.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/** iOS driver. Not compiled/verified in this sandbox (no macOS/Xcode host — README-BUILD.md §4). */
public actual class DatabaseDriverFactory {
    public actual fun createDriver(): SqlDriver = NativeSqliteDriver(SearchDatabase.Schema, "omnilife-search.db")
}
