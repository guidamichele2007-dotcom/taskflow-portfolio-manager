package com.omnilife.core.search

/**
 * Incremental indexing (SRCH-001 extended card: "indice aggiornato in
 * transazione con le scritture — mai risultati fantasma o mancanti dopo
 * una modifica", SRCH-AC-02). A future `domain-*` repository calls
 * [index]/[remove] inside the **same** database transaction as its own
 * entity write for that guarantee to actually hold — this module cannot
 * enforce that from the outside, only provide the operation.
 */
public interface SearchIndexer {
    /** Upsert: replaces any existing index entry for [entity.id]. */
    public fun index(entity: IndexableEntity)

    public fun remove(entityId: String)

    /**
     * Full rebuild from [entities] — the index is a derived, reconstructible
     * projection (Technical Architecture Bible §13, MFC §4 "degradato"): a
     * corrupted index recovers by calling this with every active module's
     * current entities, not by any repair logic inside the index itself.
     */
    public fun rebuild(entities: List<IndexableEntity>)

    public fun count(): Long
}
