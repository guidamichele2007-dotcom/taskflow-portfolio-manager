package com.omnilife.domain.account.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/** iOS driver. Not compiled/verified in this sandbox (no macOS/Xcode host — see README-BUILD.md §4). */
public actual class DatabaseDriverFactory {
    public actual fun createDriver(): SqlDriver = NativeSqliteDriver(AccountDatabase.Schema, "omnilife-account.db")
}
