package com.omnilife.core.notifications

import kotlinx.datetime.Instant
import kotlin.system.measureNanoTime
import kotlin.test.Test

/**
 * Hand-rolled micro-benchmark (see sprint3_report.md for why not JMH), same pattern as
 * `core-sync`/`core-security`. NTF-007's in-app center reads [NotificationHistoryStore.recent]
 * on every open — this measures it at a volume far beyond what NTF-002's 3/day budget could ever
 * accumulate in real use (10,000 entries is years of history), to bound worst case.
 */
class NotificationHistoryBenchmark {
    @Test
    fun `benchmark - record throughput and recent() latency at 10,000 entries`() {
        val store = InMemoryNotificationHistoryStore()
        val category = NotificationCategory("task.reminder", "task")
        val entryCount = 10_000

        val recordElapsedNanos =
            measureNanoTime {
                repeat(entryCount) { i ->
                    store.record(
                        NotificationRequest(
                            id = "r$i",
                            category = category,
                            priority = NotificationPriority.UTILE,
                            entityReference = EntityReference("task-$i", "task"),
                            title = "t$i",
                            body = "b$i",
                            scheduledFor = Instant.fromEpochSeconds(i.toLong()),
                        ),
                    )
                }
            }

        val recentElapsedNanos = measureNanoTime { store.recent(50) }

        val recordMs = recordElapsedNanos / 1_000_000
        val recordsPerSecond = entryCount / (recordElapsedNanos / 1_000_000_000.0)
        println(
            "[benchmark] NotificationHistoryStore.record: $entryCount entries in ${recordMs}ms " +
                "(${"%.2f".format(recordsPerSecond)} records/s)",
        )
        val recentMs = recentElapsedNanos / 1_000_000
        println("[benchmark] NotificationHistoryStore.recent(50) at 10,000 entries: ${recentMs}ms")
    }
}
