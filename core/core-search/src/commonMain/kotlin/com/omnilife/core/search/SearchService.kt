package com.omnilife.core.search

/**
 * Servizio di Ricerca: indice full-text derivato e ricostruibile dalle entità dei moduli attivi.
 *
 * Functional Bible SRCH-001…006; Data Model Bible §11 (indice come proiezione derivata); Technical Architecture
 * Bible §13 §4.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface SearchService
