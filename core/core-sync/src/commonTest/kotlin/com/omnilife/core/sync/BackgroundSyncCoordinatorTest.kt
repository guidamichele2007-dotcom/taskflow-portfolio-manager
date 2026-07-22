package com.omnilife.core.sync

import com.omnilife.core.common.OmniResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

private class ScriptedTransport(private val script: (OutboxItem) -> RemoteAcceptance) : RemoteSyncTransport {
    override suspend fun send(item: OutboxItem): RemoteAcceptance = script(item)
}

private class ThrowingTransport(private val error: Exception) : RemoteSyncTransport {
    override suspend fun send(item: OutboxItem): RemoteAcceptance = throw error
}

class BackgroundSyncCoordinatorTest {
    private fun item(id: String) = OutboxItem(id, byteArrayOf(1), LogicalTimestamp(1, "device-a"), isHot = false)

    private fun coordinator(
        outbox: SyncOutboxStore,
        transport: RemoteSyncTransport,
        networkMonitor: NetworkMonitor = ManualNetworkMonitor(),
        stateManager: SyncStateManager = InMemorySyncStateManager(),
    ) = BackgroundSyncCoordinator(outbox, SyncScheduler(outbox), networkMonitor, stateManager, transport)

    @Test
    fun `runOnce does nothing and reports OFFLINE while the network is down`() =
        runTest {
            val outbox = InMemorySyncOutboxStore().apply { enqueue(item("a")) }
            val stateManager = InMemorySyncStateManager()
            val coord =
                coordinator(
                    outbox,
                    ScriptedTransport { RemoteAcceptance.ACCEPTED },
                    networkMonitor = ManualNetworkMonitor(initiallyOnline = false),
                    stateManager = stateManager,
                )

            val result = coord.runOnce(Instant.fromEpochSeconds(0))

            assertEquals(0, assertIs<OmniResult.Success<Int>>(result).value)
            assertEquals(SyncPhase.OFFLINE, stateManager.current().phase)
            assertEquals(1, outbox.size())
        }

    @Test
    fun `runOnce acknowledges every accepted item and empties the outbox`() =
        runTest {
            val outbox =
                InMemorySyncOutboxStore().apply {
                    enqueue(item("a"))
                    enqueue(item("b"))
                }
            val stateManager = InMemorySyncStateManager()
            val transport = ScriptedTransport { RemoteAcceptance.ACCEPTED }
            val coord = coordinator(outbox, transport, stateManager = stateManager)

            val result = coord.runOnce(Instant.fromEpochSeconds(100))

            assertEquals(2, assertIs<OmniResult.Success<Int>>(result).value)
            assertEquals(0, outbox.size())
            assertEquals(SyncPhase.IDLE, stateManager.current().phase)
            assertEquals(Instant.fromEpochSeconds(100), stateManager.current().lastSuccessfulSyncAt)
        }

    @Test
    fun `runOnce leaves a rejected item queued and reports the failure`() =
        runTest {
            val outbox = InMemorySyncOutboxStore().apply { enqueue(item("a")) }
            val stateManager = InMemorySyncStateManager()
            val transport = ScriptedTransport { RemoteAcceptance.REJECTED }
            val coord = coordinator(outbox, transport, stateManager = stateManager)

            val result = coord.runOnce(Instant.fromEpochSeconds(0))

            assertIs<OmniResult.Failure>(result)
            assertEquals(1, outbox.size())
            assertEquals(SyncPhase.ERROR, stateManager.current().phase)
        }

    @Test
    fun `runOnce surfaces a transport exception as a typed failure without crashing`() =
        runTest {
            val outbox = InMemorySyncOutboxStore().apply { enqueue(item("a")) }
            val coord = coordinator(outbox, ThrowingTransport(RuntimeException("connection reset")))

            val result = coord.runOnce(Instant.fromEpochSeconds(0))

            val failure = assertIs<OmniResult.Failure>(result)
            assertIs<SyncError.TransportFailure>(failure.error)
            assertEquals(1, outbox.size())
        }

    @Test
    fun `acknowledging a duplicate delivery for an already-removed item is idempotent`() =
        runTest {
            val outbox = InMemorySyncOutboxStore().apply { enqueue(item("a")) }
            val coord = coordinator(outbox, ScriptedTransport { RemoteAcceptance.ACCEPTED })

            coord.runOnce(Instant.fromEpochSeconds(0))
            // A duplicate transport-level delivery re-acknowledging the same id must not throw.
            outbox.acknowledge("a")

            assertEquals(0, outbox.size())
        }

    @Test
    fun `hasPersistentFailure is false until RetryEngine's threshold has elapsed`() =
        runTest {
            val outbox = InMemorySyncOutboxStore().apply { enqueue(item("a")) }
            val coord = coordinator(outbox, ScriptedTransport { RemoteAcceptance.REJECTED })
            val start = Instant.fromEpochSeconds(0)

            coord.runOnce(start)

            assertTrue(!coord.hasPersistentFailure("a", start))
            assertTrue(coord.hasPersistentFailure("a", start + RetryEngine.persistentFailureThreshold))
        }
}
