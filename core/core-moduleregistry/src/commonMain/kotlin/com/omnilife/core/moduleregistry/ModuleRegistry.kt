package com.omnilife.core.moduleregistry

/**
 * Registro Moduli: stato di attivazione/disattivazione di ogni modulo (DM-SYS-03 ModuleActivation) — unica fonte di
 * verità su quali moduli sono invocabili.
 *
 * Data Model Bible §01 (DM-SYS-03); Technical Architecture Bible §04 (ciclo di vita di un modulo).
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface ModuleRegistry
