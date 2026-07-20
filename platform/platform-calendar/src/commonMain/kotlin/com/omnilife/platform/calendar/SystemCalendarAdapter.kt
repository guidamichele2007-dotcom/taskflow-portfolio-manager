package com.omnilife.platform.calendar

/**
 * Adattatore verso il calendario di sistema (EventKit/CalendarProvider).
 *
 * Technical Architecture Bible §01 §5; Functional Bible CAL-001.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface SystemCalendarAdapter
