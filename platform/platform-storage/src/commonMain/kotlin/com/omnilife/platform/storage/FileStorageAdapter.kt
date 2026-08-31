package com.omnilife.platform.storage

/**
 * Adattatore di storage file locale (allegati, cache).
 *
 * Technical Architecture Bible §01 §5.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface FileStorageAdapter
