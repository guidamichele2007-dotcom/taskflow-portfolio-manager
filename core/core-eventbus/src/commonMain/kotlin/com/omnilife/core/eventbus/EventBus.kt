package com.omnilife.core.eventbus

/**
 * Bus Eventi: pubblicazione/sottoscrizione locale al dispositivo tra moduli di dominio, senza dipendenze dirette tra loro.
 *
 * Technical Architecture Bible §03 (Event-Driven Architecture); Functional Bible MFC §8.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface EventBus
