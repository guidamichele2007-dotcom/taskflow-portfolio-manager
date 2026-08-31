package com.omnilife.core.search.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

/** JVM driver: in-memory SQLite — the verifiable target in this sandbox (README-BUILD.md §4). */
public actual class DatabaseDriverFactory {
    public actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        SearchDatabase.Schema.create(driver)
        return driver
    }
}
