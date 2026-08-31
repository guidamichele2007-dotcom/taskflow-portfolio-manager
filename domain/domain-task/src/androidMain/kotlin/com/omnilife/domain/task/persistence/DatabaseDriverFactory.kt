package com.omnilife.domain.task.persistence

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android driver. Not compiled/verified in this sandbox (no Android SDK —
 * see README-BUILD.md §4); standard SQLDelight Android driver usage.
 */
public actual class DatabaseDriverFactory(private val context: Context) {
    public actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(TaskDatabase.Schema, context, "omnilife-task.db")
}
