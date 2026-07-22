package com.omnilife.core.sync

/**
 * Last-Writer-Wins register (TDR-24, Data Model Bible §11 §6): the CRDT for
 * a single scalar field (MFC-R-08 — "modifiche concorrenti convergono
 * automaticamente per-campo... vince la modifica più recente"). [merge] is
 * commutative and idempotent by construction — the two properties a real
 * CRDT must have (verified by [LwwRegisterConvergenceTest]).
 */
public data class LwwRegister<out T>(public val value: T, public val timestamp: LogicalTimestamp) {
    public companion object {
        /**
         * The later [LogicalTimestamp] wins; on an exact tie (impossible in
         * practice since `deviceId` breaks it, but total-order requires a
         * deterministic answer regardless) `a` is kept — never a coin flip,
         * "l'ordine di arrivo non altera il risultato finale" (MFC §3).
         */
        public fun <T> merge(
            a: LwwRegister<T>,
            b: LwwRegister<T>,
        ): LwwRegister<T> = if (b.timestamp > a.timestamp) b else a
    }
}
