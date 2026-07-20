package com.omnilife.platform.health

/**
 * Adattatore verso la piattaforma Salute di sistema (HealthKit/Health Connect). Mai sincronizzato sul cloud.
 *
 * Technical Architecture Bible §01 §5; Functional Bible HLTH-001, HLTH-R-02.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface SystemHealthAdapter
