# shared

**Scopo**: Modulo aggregatore: espone al layer nativo (androidApp, iosApp) l'insieme dei moduli core/domain come un'unica libreria (per iOS: confine di export del framework). Non contiene logica propria.

**Riferimento**: Technology Decision Record, TDR-01 ("Kotlin Multiplatform per il dominio condiviso"), TDR-18

**Stato**: infrastruttura di bootstrap (Engineering Plan, EPIC-00) — nessuna logica di business, nessuna implementazione oltre alla struttura dichiarata qui. Vedi [../../README-BUILD.md](../../README-BUILD.md) per le convenzioni comuni a ogni modulo.
