package com.omnilife.platform.persistence

/**
 * Adattatore di persistenza locale. NESSUNA implementazione di database in questo bootstrap.
 *
 * Technical Architecture Bible §01 §5 ("Persistenza locale"); TDR-06 (SQLite cifrato + FTS).
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface LocalPersistenceAdapter
