package com.omnilife.core.notifications

/**
 * Broker centrale delle Notifiche: budget, raggruppamento, silenzi. Nessun modulo notifica direttamente.
 *
 * Functional Bible NTF-001…008; Technical Architecture Bible §13 §4.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface NotificationBroker
