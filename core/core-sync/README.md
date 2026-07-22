# core:core-sync

**Scopo**: Motore di Sincronizzazione — CRDT minimale su misura, coda offline, retry, scheduler (Sprint 3, Core Platform).

**Riferimento**: Functional Bible MFC §3; Data Model Bible §8/§11; Technical Architecture Bible §05; TDR-05, TDR-24.

## Componenti

| Componente | File | Ruolo |
|---|---|---|
| `LogicalTimestamp` | `LogicalTimestamp.kt` | "Vettore di versione" minimo `(counter, deviceId)` — mai wall-clock (MFC-E-10) |
| `LwwRegister<T>` | `LwwRegister.kt` | CRDT per campo scalare (MFC-R-08) |
| `ORSet<T>` | `ORSet.kt` | CRDT per insiemi di riferimenti (GraphLink, INV-04) |
| `SnapshotHistory<T>` | `SnapshotHistory.kt` | Versionamento a snapshot per le Note (NOTE-006) — merge per paragrafo **non implementato**, vedi report |
| `EntityFieldMerger` | `EntityFieldMerger.kt` | Il "conflict resolver": merge campo-per-campo di un'intera entità, generico |
| `SyncOutboxStore` | `SyncOutbox.kt` | Coda offline persistente, priorità dati caldi (MFC §3) |
| `RetryEngine` | `RetryEngine.kt` | Backoff esponenziale + soglia di notifica a 72h (MFC §3) |
| `SyncScheduler` | `SyncScheduler.kt` | Sospensione batteria/rete a consumo (MFC-E-04, MFC §3) |
| `RecurrenceOccurrenceStore` | `RecurrenceIdempotency.kt` | Chiave di idempotenza `(regola, periodo)` — il blocco TASK-AC-05 segnalato da Sprint 1/2, pronto per l'adozione |
| `SyncEngine` | `SyncEngine.kt` | Facciata che compone i precedenti |

## Cosa NON fa questo modulo

- Non dipende da `domain-task` né da alcun modulo `domain-*` — opera solo su tipi generici (`Map<String, LwwRegister<Any?>>`, `OutboxItem`). Collegare `domain-task`'s `CompleteTask`/`Envelope` a questi tipi è un blocco esplicito per Sprint 4.
- Non implementa il trasporto verso L6 (protocollo di rete) — Technical Architecture Bible §05 §6 lo dichiara esplicitamente rinviato.
- Non implementa il merge per paragrafo per le Note — usa il fallback whole-snapshot-LWW che la Data Model Bible stessa permette.

## Verificato in questo sandbox

Interamente Kotlin puro (nessuna dipendenza da SQLDelight/Compose) — compilato e testato sul target JVM, ma senza alcuna dipendenza da piattaforma: **funzionerebbe identico su ogni target KMP**, incluso Android/iOS, senza bisogno di `expect`/`actual`.

Vedi [../../README-BUILD.md](../../README-BUILD.md) per le convenzioni comuni a ogni modulo.
