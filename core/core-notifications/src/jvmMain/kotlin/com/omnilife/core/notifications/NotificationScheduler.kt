package com.omnilife.core.notifications

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * JVM `actual`: `ScheduledExecutorService` (TDR-26) — real and verified in this sandbox, also
 * useful for a future desktop build (README-BUILD.md §4).
 */
public actual class NotificationScheduler {
    private val executor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "omnilife-notification-scheduler").apply { isDaemon = true }
        }
    private val pending = ConcurrentHashMap<String, ScheduledFuture<*>>()

    public actual fun schedule(
        requestId: String,
        at: Instant,
        onFire: () -> Unit,
    ) {
        cancel(requestId)
        val delayMillis = (at - Clock.System.now()).inWholeMilliseconds.coerceAtLeast(0)
        val future =
            executor.schedule(
                {
                    pending.remove(requestId)
                    onFire()
                },
                delayMillis,
                TimeUnit.MILLISECONDS,
            )
        pending[requestId] = future
    }

    public actual fun cancel(requestId: String) {
        pending.remove(requestId)?.cancel(false)
    }

    public actual fun cancelAll() {
        pending.keys.toList().forEach { cancel(it) }
    }
}
