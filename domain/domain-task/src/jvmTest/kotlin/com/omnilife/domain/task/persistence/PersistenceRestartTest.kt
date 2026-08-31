package com.omnilife.domain.task.persistence

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.omnilife.core.eventbus.InMemoryEventBus
import com.omnilife.domain.task.usecase.CreateTask
import com.omnilife.domain.task.usecase.Edit
import com.omnilife.domain.task.usecase.NewTaskDetails
import com.omnilife.domain.task.usecase.TaskFieldEdits
import com.omnilife.domain.task.usecase.UpdateTaskFields
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * MVP Release 1.0, Fase 8: every other persistence test in this repository (including this
 * module's own `SqlDelightTaskRepositoryTest`) uses `JdbcSqliteDriver.IN_MEMORY` — deliberately,
 * per its own doc comment, "the verifiable stand-in target in this sandbox" (no Android
 * SDK/emulator here to exercise the real `AndroidSqliteDriver` file path). But an in-memory
 * database is lost the instant its single connection closes, by SQLite's own design — it cannot,
 * even in principle, prove data survives "close the app, reopen the app." This test uses a real
 * temp *file* on disk instead, opening a **second, independent** `JdbcSqliteDriver`/repository
 * against the same path after the first is done with it — the closest this sandbox can get to
 * genuinely proving the create→persist→restart→still-there cycle Fase 8 asks for, rather than
 * asserting it by inspection of `AndroidSqliteDriver`'s (unverified here) construction alone.
 */
class PersistenceRestartTest {
    @Test
    fun `a task created, then edited, survives closing one connection and opening a fresh one on the same file`() =
        runTest {
            val dbFile = Files.createTempFile("omnilife-persistence-restart-test", ".db")
            try {
                val dbPath = dbFile.toAbsolutePath().toString()

                // "Session 1": app runs, creates and edits a task.
                val firstDriver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
                TaskDatabase.Schema.create(firstDriver)
                val firstRepository = SqlDelightTaskRepository(firstDriver)
                val eventBus = InMemoryEventBus()
                CreateTask(firstRepository, eventBus, newId = { "restart-task-1" })(
                    "Renew driver's license",
                    listId = "list-1",
                    ownerAccountId = "acc-1",
                    deviceId = "dev-1",
                    details = NewTaskDetails(dueDate = LocalDate(2026, 9, 1)),
                )
                UpdateTaskFields(firstRepository, eventBus)(
                    "restart-task-1",
                    TaskFieldEdits(title = Edit.Set("Renew driver's license (updated)")),
                )
                firstDriver.close()

                // "Session 2": a brand-new connection, brand-new repository instance, same file --
                // nothing here shares in-process state with session 1 above.
                val secondDriver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
                val secondRepository = SqlDelightTaskRepository(secondDriver)
                val reloaded = secondRepository.findTaskById("restart-task-1")

                assertEquals("Renew driver's license (updated)", reloaded?.title)
                assertEquals(LocalDate(2026, 9, 1), reloaded?.dueDate)
                secondDriver.close()
            } finally {
                Files.deleteIfExists(dbFile)
            }
        }
}
