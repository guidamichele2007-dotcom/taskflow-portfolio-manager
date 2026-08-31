package com.omnilife.core.capture

/**
 * Parser di Cattura: instradamento del testo/voce catturato verso il modulo di destinazione o l'Inbox. Servizio
 * Core (L4), non modulo di dominio.
 *
 * Functional Bible CAPT-001…010; Technical Architecture Bible §02 §1 ("Cattura, servizio Core"), §13 §4.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface CaptureParser
