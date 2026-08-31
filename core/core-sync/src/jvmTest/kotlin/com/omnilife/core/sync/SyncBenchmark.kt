package com.omnilife.core.sync

import kotlin.system.measureNanoTime
import kotlin.test.Test

/**
 * Hand-rolled micro-benchmark (see sprint3_report.md for why not JMH), run
 * as a normal JVM test. Simulates the scale MFC-E-14 declares (100,000+
 * entities) for the two operations every sync round performs: merging one
 * entity's fields (LWW) and merging two devices' whole link sets (OR-Set).
 *
 * The OR-Set inputs are built directly from maps rather than via 100,000
 * sequential [ORSet.add] calls: each `add` copies the whole backing map
 * (an immutable `Map`, no persistent/structural-sharing data structure in
 * the Kotlin stdlib), so a tight sequential-add loop is O(n²) — fine for
 * how the type is actually used (one add per real user action, not a bulk
 * loop) but the wrong way to construct a 100,000-element benchmark input.
 * [ORSet.merge] itself — the actual "a sync round arrives" operation this
 * benchmark targets — is a single O(n) union, measured directly below.
 */
class SyncBenchmark {
    @Test
    fun `benchmark - EntityFieldMerger throughput at MFC-E-14 scale (100,000 entities)`() {
        val entityCount = 100_000
        val localVersion =
            (0 until entityCount).associate { i ->
                "entity-$i" to mapOf("title" to LwwRegister("local $i", LogicalTimestamp(i.toLong(), "device-a")))
            }
        val remoteVersion =
            (0 until entityCount).associate { i ->
                val timestamp = LogicalTimestamp((i + 1).toLong(), "device-b")
                "entity-$i" to mapOf("title" to LwwRegister("remote $i", timestamp))
            }

        val elapsedNanos =
            measureNanoTime {
                localVersion.keys.forEach { id ->
                    EntityFieldMerger.merge(localVersion.getValue(id), remoteVersion.getValue(id))
                }
            }

        println(
            "[benchmark] EntityFieldMerger.merge: $entityCount entities in ${elapsedNanos / 1_000_000}ms " +
                "(${"%.2f".format(entityCount / (elapsedNanos / 1_000_000_000.0))} merges/s)",
        )
    }

    @Test
    fun `benchmark - ORSet merge throughput at 100,000 links per device`() {
        val entityCount = 100_000
        val deviceATags =
            (0 until entityCount).associate { i -> "link-$i-a" to setOf(LogicalTimestamp(i.toLong(), "device-a")) }
        val deviceBTags =
            (0 until entityCount).associate { i -> "link-$i-b" to setOf(LogicalTimestamp(i.toLong(), "device-b")) }
        val deviceA = ORSet.fromAddedTags(deviceATags)
        val deviceB = ORSet.fromAddedTags(deviceBTags)

        val elapsedNanos = measureNanoTime { deviceA.merge(deviceB) }

        println("[benchmark] ORSet.merge: ${entityCount * 2} total tags in ${elapsedNanos / 1_000_000}ms")
    }
}
