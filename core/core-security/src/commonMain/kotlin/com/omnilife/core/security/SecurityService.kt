package com.omnilife.core.security

/**
 * Servizio di Sicurezza: contratto della gerarchia di cifratura e dello sblocco locale. NESSUNA implementazione in
 * questo bootstrap.
 *
 * Functional Bible SEC-001…003, MFC §5; Technical Architecture Bible §10; TDR-04.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface SecurityService
