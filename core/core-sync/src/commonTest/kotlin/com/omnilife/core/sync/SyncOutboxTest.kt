package com.omnilife.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** MFC §3: "l'outbox non si svuota mai senza conferma del server"; "Priorità: dati di oggi/settimana → resto". */
class SyncOutboxTest {
    @Test
    fun `hot items are returned before cold items regardless of enqueue order`() {
        val outbox = InMemorySyncOutboxStore()
        outbox.enqueue(OutboxItem("cold-1", byteArrayOf(1), LogicalTimestamp(1, "device-a"), isHot = false))
        outbox.enqueue(OutboxItem("hot-1", byteArrayOf(2), LogicalTimestamp(2, "device-a"), isHot = true))

        assertEquals("hot-1", outbox.peekNext()?.id)
    }

    @Test
    fun `among equally hot items, the oldest is returned first`() {
        val outbox = InMemorySyncOutboxStore()
        outbox.enqueue(OutboxItem("newer", byteArrayOf(1), LogicalTimestamp(5, "device-a"), isHot = true))
        outbox.enqueue(OutboxItem("older", byteArrayOf(2), LogicalTimestamp(1, "device-a"), isHot = true))

        assertEquals("older", outbox.peekNext()?.id)
    }

    @Test
    fun `an item stays queued until acknowledge is called`() {
        val outbox = InMemorySyncOutboxStore()
        val item = OutboxItem("item-1", byteArrayOf(1), LogicalTimestamp(1, "device-a"), isHot = true)
        outbox.enqueue(item)

        outbox.peekNext()

        assertEquals(1, outbox.size())
        outbox.acknowledge("item-1")
        assertEquals(0, outbox.size())
        assertNull(outbox.peekNext())
    }
}
