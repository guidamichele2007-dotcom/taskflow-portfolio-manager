package com.omnilife.platform.security

/**
 * Adattatore verso l'enclave di sicurezza/biometria del dispositivo. NESSUNA implementazione crittografica in
 * questo bootstrap.
 *
 * Technical Architecture Bible §01 §5, §10; TDR-04.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface SecureEnclaveAdapter
