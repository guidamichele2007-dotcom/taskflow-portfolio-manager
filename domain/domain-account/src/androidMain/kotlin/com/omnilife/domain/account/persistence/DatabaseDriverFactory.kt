package com.omnilife.domain.account.persistence

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/** Android driver. Not compiled/verified in this sandbox (no Android SDK — see README-BUILD.md §4). */
public actual class DatabaseDriverFactory(private val context: Context) {
    public actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(AccountDatabase.Schema, context, "omnilife-account.db")
}
