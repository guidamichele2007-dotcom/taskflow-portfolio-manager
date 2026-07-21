package com.omnilife.domain.task.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

/**
 * JVM driver: in-memory SQLite (production would point at a file path
 * outside this bootstrap's concern — see README-BUILD.md §4, this is the
 * verifiable stand-in target in this sandbox).
 */
public actual class DatabaseDriverFactory {
    public actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TaskDatabase.Schema.create(driver)
        return driver
    }
}
