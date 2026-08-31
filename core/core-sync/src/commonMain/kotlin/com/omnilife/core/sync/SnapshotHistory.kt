package com.omnilife.core.sync

/**
 * Snapshot-with-history versioning for long-form content (TDR-24, Data
 * Model Bible §11 §2/§6 — Note's declared derogation, NOTE-006/NOTE-AC-03):
 * the whole snapshot follows [LwwRegister]'s rule (most recent wins by
 * [LogicalTimestamp]), and the losing snapshot is **kept**, never dropped
 * (INV-07: restoring a prior version is always a new history entry, never
 * deletes later ones).
 *
 * Per-paragraph merge ("dove non ambiguo", the Bible's stated ideal for
 * genuinely concurrent edits to different paragraphs) is **not implemented
 * in this sprint** — this is the documented fallback the Bible itself
 * allows ("altrove, l'intero snapshot segue la regola per-campo"), not a
 * silent shortfall. See sprint3_report.md's proposed-improvements section.
 */
public data class SnapshotHistory<T>(public val current: LwwRegister<T>, public val history: List<LwwRegister<T>>) {
    public companion object {
        public fun <T> merge(
            a: SnapshotHistory<T>,
            b: SnapshotHistory<T>,
        ): SnapshotHistory<T> {
            val winner = LwwRegister.merge(a.current, b.current)
            val loser = if (winner === a.current) b.current else a.current
            val mergedHistory = (a.history + b.history + loser).distinct().sortedByDescending { it.timestamp }
            return SnapshotHistory(winner, mergedHistory.filterNot { it == winner })
        }

        public fun <T> initial(
            value: T,
            timestamp: LogicalTimestamp,
        ): SnapshotHistory<T> = SnapshotHistory(LwwRegister(value, timestamp), emptyList())
    }
}
