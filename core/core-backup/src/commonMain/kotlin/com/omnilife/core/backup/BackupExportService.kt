package com.omnilife.core.backup

/**
 * Motore di Backup/Export: contratto di snapshot, ripristino ed esportazione. NESSUNA implementazione in questo bootstrap.
 *
 * Functional Bible BKP-001…004, EXP-001…003; Technical Architecture Bible §13 §4.
 *
 * Contratto soltanto: nessuna implementazione in questo modulo (bootstrap,
 * Engineering Plan EPIC-00). Le implementazioni concrete vivono nei moduli
 * platform-* o nei rispettivi motori applicativi, quando le funzioni
 * corrispondenti entreranno in sviluppo.
 */
public interface BackupExportService
