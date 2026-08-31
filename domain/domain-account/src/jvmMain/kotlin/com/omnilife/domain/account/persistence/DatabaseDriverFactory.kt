package com.omnilife.domain.account.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

/** JVM driver: in-memory SQLite, same convention as `domain-task`'s. */
public actual class DatabaseDriverFactory {
    public actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AccountDatabase.Schema.create(driver)
        return driver
    }
}
