package com.omnilife.core.sync

/**
 * Motore di Sincronizzazione: contratto di convergenza offline-first (CRDT minimale). NESSUNA implementazione in
 * questo bootstrap.
 *
 * Functional Bible MFC §3; Data Model Bible §8/§11; Technical Architecture Bible §05; TDR-05.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface SyncEngine
