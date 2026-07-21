package com.omnilife.core.graph

/**
 * Grafo dei collegamenti (GraphLink): l'unico tipo di relazione cross-modulo, posseduto dal Core, non da un modulo
 * di dominio.
 *
 * Data Model Bible §02 (DM-LINK-01 GraphLink), MDEC-02; Technical Architecture Bible §02.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface GraphRepository
