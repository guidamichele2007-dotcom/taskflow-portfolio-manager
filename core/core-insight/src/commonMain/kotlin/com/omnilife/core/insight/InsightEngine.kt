package com.omnilife.core.insight

/**
 * Motore di Insight: osservatore passivo degli eventi di dominio, regole dichiarative, nessuna porta di rete per
 * costruzione.
 *
 * Functional Bible INS-001…005; Technical Architecture Bible §10 §5 ("nessuna porta verso la rete: vincolo
 * architetturale").
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface InsightEngine
