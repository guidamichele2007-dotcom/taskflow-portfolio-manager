# core:core-sync

**Scopo**: Motore di Sincronizzazione offline-first condiviso dall'intera piattaforma — CRDT minimale su misura, coda offline persistente, retry, scheduler, tracciamento versioni/modifiche, merge, connettività, stato osservabile (Sprint 3 — Core Sync Engine).

**Riferimento**: Functional Bible MFC §3; Data Model Bible §8/§11; Technical Architecture Bible §05; TDR-05, TDR-21, TDR-24.

## Componenti

| Componente richiesto | Tipo/file | Ruolo |
|---|---|---|
| CRDT Engine | `LogicalTimestamp.kt`, `LwwRegister.kt`, `ORSet.kt`, `SnapshotHistory.kt` | Le primitive CRDT: "vettore di versione" minimo, register LWW, insieme OR-Set, snapshot con storia |
| Conflict Resolver | `ConflictResolver.kt` (`LwwConflictResolver`) | Risoluzione per-campo con tracciamento esplicito di "c'è stato un conflitto?" (`FieldResolution.hadConflict`) |
| Merge Engine | `MergeEngine.kt` (`MergeableEntitySnapshot`) | Orchestratore: merge di un'intera entità (campi + insiemi di relazioni) in una sola chiamata |
| — (helper generico, precede questo sprint) | `EntityFieldMerger.kt` | Merge campo-per-campo di due mappe di `LwwRegister`, usato internamente da `MergeEngine` |
| Version Manager | `VersionManager.kt` (`InMemoryVersionManager`) | Bookkeeping per-entità: qual è la versione registrata, è arrivata una versione più recente? — la base della sincronizzazione incrementale |
| Change Tracker | `ChangeTracker.kt` (`InMemoryChangeTracker`) | Insieme "dirty" locale: cosa è cambiato dall'ultimo sync riuscito, indipendente dai payload già in coda |
| Delta Generator | `DeltaGenerator.kt` | Genera il delta campo-per-campo tra due stati della stessa entità — solo i campi cambiati attraversano la rete |
| Local Change Queue | `SyncOutbox.kt` (`SyncOutboxStore`, `InMemorySyncOutboxStore`), `persistence/SqlDelightSyncOutboxStore.kt` | Coda offline; l'implementazione SQLDelight **sopravvive al kill del processo** (vedi sotto) |
| Retry Engine | `RetryEngine.kt` | Backoff esponenziale (5s→1h) + soglia di fallimento persistente a 72h (MFC §3) |
| Sync Scheduler | `SyncScheduler.kt` | Priorità dati caldi, sospensione batteria, rete a consumo (MFC-E-04, MFC §3) |
| Network Monitor | `NetworkMonitor.kt` (`ManualNetworkMonitor`) | Segnale di connettività testabile; l'integrazione con le API di piattaforma è rinviata (vedi sotto) |
| Sync State Manager | `SyncStateManager.kt` (`SyncPhase`, `SyncState`, `InMemorySyncStateManager`) | Stato di sync osservabile (`IDLE`/`SYNCING`/`OFFLINE`/`ERROR`) per un futuro indicatore, non costruito in questo sprint |
| Background Sync | `BackgroundSyncCoordinator.kt` (`RemoteSyncTransport`) | Un round di sync: preleva dalla coda, invia via trasporto, aggiorna stato — idempotente e mai lanciante |
| Sync Engine (facciata) | `SyncEngine.kt` | Compone tutti i componenti stateless sopra con implementazioni in-memory di default |
| — (già presente, non richiesto esplicitamente) | `RecurrenceIdempotency.kt` | Chiave di idempotenza `(regola, periodo)` per occorrenze ricorrenti (TASK-AC-05) |
| Errori | `SyncError.kt` | `TransportFailure`, `RemoteRejected`, `PersistentFailure` — vocabolario `DomainError` (TDR-21) |

## La coda persistente (crash resilience)

`InMemorySyncOutboxStore` perde la coda al primo crash — non soddisfa MFC §3 ("l'outbox
persistente, sopravvive al kill"). `SqlDelightSyncOutboxStore` (in `persistence/`) la
sostituisce con una tabella SQLite reale (`SyncOutbox.sq`, tabella `outboxRow`, nominata così
per non collidere con il tipo di dominio `OutboxItem`). `SqlDelightSyncOutboxStoreTest`
verifica la resistenza al crash riaprendo lo store con un nuovo driver puntato allo stesso
file — non un semplice test di CRUD, ma la riprova che i dati sopravvivono davvero alla
"morte" del processo che li ha scritti.

## Cosa NON fa questo modulo

- Non dipende da `domain-task` né da alcun modulo `domain-*` — opera solo su tipi generici
  (`Map<String, LwwRegister<Any?>>`, `OutboxItem`, `MergeableEntitySnapshot`). Collegare
  `domain-task` a questi tipi resta un blocco esplicito per Sprint 4.
- Non implementa `RemoteSyncTransport` — il protocollo di rete verso il backend (Technical
  Architecture Bible §05 §6) è esplicitamente rinviato; `BackgroundSyncCoordinator` è
  verificato contro trasporti finti.
- Non implementa il merge per paragrafo per le Note — usa il fallback whole-snapshot-LWW che
  la Data Model Bible stessa permette.
- `NetworkMonitor` ha solo l'implementazione manuale/testabile: i connettori di piattaforma
  reali (Android `ConnectivityManager`, iOS `NWPathMonitor`) sono un blocco per Sprint 4.
- Non implementa cifratura (esplicitamente escluso da questo sprint — vedi `core-security`)
  né notifiche (`core-notifications`, descoped da questo sprint).
- Nessuna UI, nessuna schermata.

## Verificato in questo sandbox

Le primitive CRDT e i componenti stateless (`ConflictResolver`, `MergeEngine`,
`VersionManager`, `ChangeTracker`, `DeltaGenerator`, `NetworkMonitor`, `SyncStateManager`,
`BackgroundSyncCoordinator`, `RetryEngine`, `SyncScheduler`) sono Kotlin puro — funzionerebbero
identici su ogni target KMP senza `expect`/`actual`. `SqlDelightSyncOutboxStore` usa lo stesso
pattern JVM/Android/iOS di `core-search` e `domain-task`: solo il target JVM è compilato e
testato in questo sandbox (nessun SDK Android, nessun host macOS/Xcode — vedi
[../../README-BUILD.md](../../README-BUILD.md) §4).
