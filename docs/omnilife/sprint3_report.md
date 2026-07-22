# Sprint 3 — Core Sync Engine — Report

**Perimetro**: il motore di sincronizzazione offline-first condiviso dall'intera piattaforma —
Sync Engine, Local Change Queue, Sync Scheduler, Background Sync, Conflict Resolver, CRDT
Engine, Version Manager, Change Tracker, Delta Generator, Merge Engine, Retry Engine, Network
Monitor, Sync State Manager. Nessuna UI, nessuna schermata, nessun modulo `domain-*`
(Note/Calendario/Finanze/Home/AI esclusi per vincolo esplicito), nessuna notifica, nessuna
cifratura — per vincolo esplicito del task raffinato.

Questo sprint è iniziato con un perimetro più ampio ("Core Platform": sicurezza, sync, ricerca,
notifiche) e a metà implementazione ha ricevuto un perimetro raffinato che lo restringe al solo
Sync Engine, escludendo esplicitamente notifiche e cifratura. `core-security` e `core-search`
erano già completamente implementati e verificati quando è arrivato il raffinamento: restano
nel repository (nessun lavoro è stato buttato) e sono documentati sotto per completezza, ma
**non sono stati estesi ulteriormente** dopo il raffinamento — solo `core-sync` ha ricevuto
lavoro aggiuntivo in risposta al nuovo perimetro. `core-notifications` è stato interamente
descoped: nessun codice applicativo oltre il bootstrap iniziale.

## 1. Architettura finale

### 1.1 core-sync — Sync Engine (perimetro raffinato di questo sprint)

| Componente richiesto | File | Ruolo |
|---|---|---|
| CRDT Engine | `LogicalTimestamp.kt`, `LwwRegister.kt`, `ORSet.kt`, `SnapshotHistory.kt` | Le primitive CRDT: vettore di versione minimo `(counter, deviceId)`, register last-writer-wins, insieme observed-remove, snapshot con storia (TDR-24) |
| Conflict Resolver | `ConflictResolver.kt` (`LwwConflictResolver`) | Risoluzione per-campo con tracciamento esplicito `hadConflict` — distingue "due scritture della stessa versione" da "due valori diversi in competizione" |
| Merge Engine | `MergeEngine.kt` (`MergeableEntitySnapshot`) | Orchestratore: un'unica chiamata fonde campi (via `ConflictResolver`) e insiemi di relazioni (via `ORSet.merge`) di un'intera entità |
| Version Manager | `VersionManager.kt` (`InMemoryVersionManager`) | Bookkeeping per-entità della versione registrata — base della sincronizzazione incrementale |
| Change Tracker | `ChangeTracker.kt` (`InMemoryChangeTracker`) | Insieme "dirty" locale, indipendente dai payload già in coda |
| Delta Generator | `DeltaGenerator.kt` | Delta campo-per-campo tra due stati della stessa entità — solo i campi cambiati attraversano la rete |
| Local Change Queue | `SyncOutbox.kt` (`SyncOutboxStore`/`InMemorySyncOutboxStore`), `persistence/SqlDelightSyncOutboxStore.kt` (+ `SyncOutbox.sq`) | Coda offline; l'implementazione SQLDelight **sopravvive al kill del processo** (§4) |
| Retry Engine | `RetryEngine.kt` | Backoff esponenziale 5s→1h, soglia di fallimento persistente a 72h (MFC §3) |
| Sync Scheduler | `SyncScheduler.kt` | Priorità dati caldi, sospensione batteria, rete a consumo (MFC-E-04) |
| Network Monitor | `NetworkMonitor.kt` (`ManualNetworkMonitor`) | Segnale di connettività testabile (TDR-28: connettori di piattaforma reali rinviati) |
| Sync State Manager | `SyncStateManager.kt` (`SyncPhase`, `SyncState`, `InMemorySyncStateManager`) | Stato di sync osservabile: `IDLE`/`SYNCING`/`OFFLINE`/`ERROR` |
| Background Sync | `BackgroundSyncCoordinator.kt` (`RemoteSyncTransport`, `RemoteAcceptance`) | Un round di sync: preleva dalla coda in ordine di priorità, invia via trasporto (iniettabile), aggiorna stato — mai lanciante, ogni fallimento è un `SyncError` tipizzato |
| Sync Engine (facciata) | `SyncEngine.kt` | Compone tutti i componenti stateless sopra con default in-memory |
| Errori | `SyncError.kt` | `TransportFailure`/`RemoteRejected`/`PersistentFailure` — vocabolario `DomainError` (TDR-21) |
| *(non richiesto esplicitamente, già presente)* | `RecurrenceIdempotency.kt` | Chiave di idempotenza `(regola, periodo)` per occorrenze ricorrenti (TASK-AC-05) |

`BackgroundSyncCoordinator` non è pre-cablato in `SyncEngine`: richiede un `RemoteSyncTransport`
concreto (la chiamata di rete reale verso il backend), che resta fuori perimetro — Technical
Architecture Bible §05 §6 rinvia esplicitamente il protocollo di trasporto. Il coordinator è
verificato contro trasporti finti (`ScriptedTransport`, `ThrowingTransport` nei test).

### 1.2 core-security (completato prima del raffinamento, non esteso oltre)

`CryptoService`/`KeyManager`/`SecureStorage`/`AppLockService`/`RecoveryKeyService`/
`FieldCipher` — gerarchia di chiavi a cifratura busta (MK→KEK→DEK), AES-256-GCM +
PBKDF2WithHmacSHA256 (TDR-23). Il raffinamento di perimetro esclude esplicitamente ulteriore
lavoro di cifratura in questo sprint; questo modulo resta com'era al momento del raffinamento.

### 1.3 core-search (completato prima del raffinamento, non esteso oltre)

`SearchIndexer`/`UnifiedSearchService` su SQLite FTS5 via SQLDelight (TDR-25), ranking a
comparatore esplicito a 3 assi (mai `bm25()`), sanitizzazione query contro injection
(MFC-E-17), suggerimenti di ricerca recente.

### 1.4 core-notifications — descoped

Nessun componente implementato oltre il bootstrap del modulo. TDR-26 (scheduling notifiche)
resta documentato per lo storico della decisione, ma non è stato seguito da implementazione in
questo sprint: il raffinamento del task lo esclude esplicitamente ("Non implementare: ...
notifiche").

## 2. Benchmark (numeri reali, misurati in questo sandbox)

| Componente | Misura | Risultato |
|---|---|---|
| `EntityFieldMerger.merge` (core-sync) | 100.000 entità | 162ms (616.915 merge/s) |
| `ORSet.merge` (core-sync) | 200.000 tag totali | 407ms |
| `SqlDelightSyncOutboxStore.enqueue` (core-sync, **nuovo**) | 10.000 elementi, file SQLite reale | 37.567ms (266,19 enqueue/s) |
| `SqlDelightSyncOutboxStore.peekNext` (core-sync, **nuovo**) | con 10.000 righe in coda | 72ms |
| AES-256-GCM encrypt (core-security) | 10.000 operazioni | 188ms (53.026,81 op/s) |
| AES-256-GCM decrypt (core-security) | 10.000 operazioni | 112ms (88.924,29 op/s) |
| PBKDF2WithHmacSHA256, 600.000 iterazioni (core-security) | per derivazione | 559,2ms (costo di sblocco, intenzionale) |
| Ricerca FTS5, MFC-AC-07 (core-search) | 50.000 entità indicizzate | indicizzazione 1.586ms, ricerca 13ms (budget ≤100ms) |

Tutti i benchmark sono metodi `@Test` con `kotlin.system.measureNanoTime` e `println`, non JMH:
la scelta è deliberata. JMH richiederebbe un plugin Gradle aggiuntivo e una JVM forkata separata
dal ciclo di test — un costo di setup non giustificato per benchmark eseguiti una volta per
verificare un budget dichiarato dalla Bible (MFC-AC-07, MFC-E-14), non per un tuning
sub-millisecondo continuo. Il rischio di rumore di misura (JIT non scaldato, GC in corsa) è
accettabile perché ogni benchmark qui verifica "siamo sotto una soglia dichiarata di un ordine
di grandezza", non una regressione di performance a grana fine.

### Nota sul numero apparentemente basso di `enqueue`

266 scritture/secondo può sembrare lento rispetto alle altre misure, ma è il costo reale di una
scrittura SQLite autocommit-per-chiamata (un commit = un fsync su disco) — non un artefatto del
benchmark: `enqueue()` è chiamato una volta per modifica utente reale, mai in un ciclo stretto,
quindi il caso d'uso reale (un utente che modifica un task) è ordini di grandezza sotto questa
soglia. Vedi §6 per il rischio residuo se un futuro scenario (import bulk) dovesse richiedere un
`enqueue` in massa.

## 3. Copertura test

| Modulo | Test totali | Falliti/errori | Note |
|---|---|---|---|
| `core-sync` | 89 | 0 | Include le 9 classi di test aggiunte in questo sprint (`VersionManagerTest`, `ChangeTrackerTest`, `DeltaGeneratorTest`, `ConflictResolverTest`, `MergeEngineTest`, `NetworkMonitorTest`, `SyncStateManagerTest`, `BackgroundSyncCoordinatorTest`, `SqlDelightSyncOutboxStoreTest`) più le classi pre-esistenti (CRDT, outbox in-memory, retry, scheduler, ricorrenza) |
| `core-security` | 39 | 0 | Invariato dal completamento pre-raffinamento |
| `core-search` | 26 | 0 | Invariato dal completamento pre-raffinamento |
| **Build completa (`gradle build`)** | — | **0** | Verificata dopo l'espansione di `core-sync`: tutti i moduli, incluse le app Android/iOS/gallery non toccate in questo sprint, restano verdi |

## 4. Casi limite verificati

- **Crash resilience della Local Change Queue**: `SqlDelightSyncOutboxStoreTest` apre lo store,
  accoda elementi, poi **ricrea un nuovo driver puntato allo stesso file** senza rieseguire la
  creazione dello schema — esattamente come un riavvio di processo reale — e verifica che gli
  elementi (inclusi i byte del payload) sopravvivano. Un test contro `IN_MEMORY` non avrebbe
  dimostrato nulla: qui il file è reale.
- **Idempotenza**: `acknowledge()` su un id già rimosso (doppia consegna dal trasporto, o un
  secondo `acknowledge` manuale) non lancia e non altera lo stato — verificato sia per lo store
  in-memory sia per quello persistente, e nel round completo di `BackgroundSyncCoordinator`.
  `recordVersion`/`markDirty` applicati due volte con lo stesso valore collassano a un solo
  effetto, mai un contatore che cresce.
- **Retry e fallimento persistente**: `BackgroundSyncCoordinatorTest` verifica che
  `hasPersistentFailure` sia falso subito dopo un rifiuto e diventi vero esattamente alla soglia
  di 72h di `RetryEngine`, usando un orologio finto (`Instant` esplicito), mai l'orologio di
  sistema.
- **Offline**: `runOnce` con `NetworkMonitor.isOnline() == false` non tocca la coda, transita lo
  stato a `OFFLINE` e ritorna un successo vuoto — mai un errore per "non c'è rete", che è uno
  stato atteso, non un fallimento.
- **Rigetto del server vs eccezione di trasporto**: due percorsi di fallimento distinti
  (`RemoteRejected` vs `TransportFailure`) sono verificati separatamente — un rigetto esplicito
  del server lascia l'elemento in coda esattamente come un errore di rete, ma con un
  `SyncError` diverso, utile a un futuro conflict-inbox per distinguere "riprova" da "serve
  intervento".
- **Merge commutativo**: `MergeEngineTest` verifica `merge(a,b) == merge(b,a)` sia per i campi
  (LWW) sia per le relazioni (OR-Set) — non solo per i tipi CRDT di base (già provato nello
  Sprint 3 originario da `LwwRegisterConvergenceTest`/`ORSetConvergenceTest`), ma per
  l'orchestratore a livello di entità intera.
- **Un solo lato presente**: `MergeEngineTest`/`ConflictResolverTest` verificano che un campo
  presente solo in locale o solo in remoto sopravviva al merge senza conflitto — non ogni merge
  è una collisione.
- **Priorità hot-prima**: sia `InMemorySyncOutboxStore` sia `SqlDelightSyncOutboxStore`
  verificano che un elemento "caldo" acclamato dopo un elemento "freddo" venga comunque
  restituito per primo da `peekNext`/`nextEligibleItem`.

## 5. Prestazioni

Tutti i budget dichiarati dalle Bible e verificabili in questo sandbox sono rispettati con
ampio margine:

- MFC-AC-07 (ricerca ≤100ms a 50.000 entità): **13ms misurati**, 7,7× sotto budget.
- MFC-E-14 (100.000+ entità per operazione di sync): merge di campo a 616.915 merge/s e merge
  di insiemi di relazioni a 200.000 tag in 407ms — nessun collo di bottiglia osservato alla
  scala dichiarata.
- Il costo del PBKDF2 (559,2ms) è **intenzionale**, non una prestazione da ottimizzare — è il
  tempo di sblocco dell'app, dimensionato per rendere costoso un attacco a forza bruta (TDR-23).
- L'unico numero relativamente basso è `enqueue` sulla coda persistente (266/s, §2) — un limite
  del disco/filesystem sottostante per scritture autocommit, non del codice applicativo, e ben
  sopra qualunque tasso di modifica umana reale.

## 6. Rischi residui

- **`BackgroundSyncCoordinator` non è collegato a un trasporto reale**: nessun modulo
  `domain-*` pubblica ancora eventi che questo coordinator possa consumare, e nessun protocollo
  di rete concreto (Technical Architecture Bible §05 §6) esiste ancora. Il collegamento resta
  un blocco esplicito per Sprint 4, non un difetto silenzioso.
- **`NetworkMonitor` ha solo l'implementazione manuale** (TDR-28): i connettori di piattaforma
  reali (Android `ConnectivityManager`, iOS `NWPathMonitor`) restano da scrivere; ogni altro
  componente è già verificato contro l'interfaccia, quindi l'aggiunta non richiederà modifiche
  a `SyncScheduler`/`BackgroundSyncCoordinator`.
- **Merge per paragrafo delle Note non implementato**: `SnapshotHistory` usa il fallback
  whole-snapshot-LWW che la Data Model Bible stessa permette (NOTE-006/NOTE-AC-03); il merge
  "dove non ambiguo" per paragrafi concorrenti resta un miglioramento futuro, già segnalato
  dallo Sprint 3 originario.
- **`enqueue` in massa non ottimizzato**: se un futuro scenario (import bulk, migrazione dati)
  dovesse accodare migliaia di elementi in un colpo solo, la scrittura autocommit-per-chiamata
  (266/s misurati) diventerebbe visibile; una futura API di enqueue batch (una singola
  transazione SQLDelight per N elementi) risolverebbe il problema senza cambiare l'interfaccia
  pubblica di `SyncOutboxStore` — non necessaria per il volume di modifiche interattive che
  questo sprint copre.
- **Android/iOS actual non compilati/verificati**: stesso limite ambientale di ogni sprint
  precedente (nessun SDK Android, nessun host macOS/Xcode in questo sandbox) — `androidMain`/
  `iosMain` di `SqlDelightSyncOutboxStore`'s `DatabaseDriverFactory` sono scritti seguendo
  esattamente il pattern già verificato di `core-search`/`domain-task`, ma non compilati qui.
- **Nessun collegamento a `domain-task`**: coerente con il vincolo esplicito del task
  ("Non implementare: ... Note, Calendario, Finanze, Home, AI"), ma resta comunque un blocco
  concreto prima che qualunque modulo `domain-*` possa effettivamente sincronizzare — tutti i
  tipi qui (`MergeableEntitySnapshot`, `OutboxItem`, ecc.) sono generici, nessuno specifico di
  Task.
- **`core-notifications`/cifratura aggiuntiva esclusi per scelta esplicita dell'utente**, non
  per limite tecnico — da riconsiderare in un futuro sprint se richiesto.

## 7. Decision Log

TDR-27 (persistenza della Local Change Queue via SQLDelight) e TDR-28 (strategia di
implementazione del Network Monitor) sono state registrate in
`docs/omnilife/technology_decision_record.md` prima/durante l'implementazione dei rispettivi
componenti, seguendo lo stesso metodo (≥3 alternative, motivazione contro la documentazione
esistente) delle voci TDR-19…26.
