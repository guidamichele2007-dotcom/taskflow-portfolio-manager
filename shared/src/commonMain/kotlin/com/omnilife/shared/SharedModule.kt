package com.omnilife.shared

/**
 * Modulo aggregatore: espone al layer nativo (androidApp, iosApp) l'insieme dei moduli core/domain come un'unica libreria (per iOS: confine di export del framework). Non contiene logica propria.
 *
 * Technology Decision Record, TDR-01 ("Kotlin Multiplatform per il dominio condiviso"), TDR-18
 *
 * Segnaposto di modulo feature (L1/L2): nessuna schermata, nessun
 * componente UI in questo bootstrap. Ospiterà lo state holder MVI
 * (Technical Architecture Bible TDR-02) quando la Feature entrerà in
 * sviluppo.
 */
public object SharedModule
