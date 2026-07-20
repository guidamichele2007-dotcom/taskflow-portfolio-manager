package com.omnilife.platform.push

/**
 * Adattatore del trasporto notifiche push (APNs/FCM). NESSUNA implementazione di rete in questo bootstrap.
 *
 * Technical Architecture Bible §01 §5; TDR-09.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface PushTransportAdapter
