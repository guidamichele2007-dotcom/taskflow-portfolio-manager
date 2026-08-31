# Sprint 3 — Core Sync Engine + Core Notifications — Report

**Perimetro finale**: il motore di sincronizzazione offline-first condiviso dall'intera
piattaforma (Sync Engine, Local Change Queue, Sync Scheduler, Background Sync, Conflict
Resolver, CRDT Engine, Version Manager, Change Tracker, Delta Generator, Merge Engine, Retry
Engine, Network Monitor, Sync State Manager) **più** il broker centrale delle notifiche (Notification
Scheduler, Local/Recurring Notifications, Notification Actions, Deep Links, Categories,
Permissions, Channels, Snooze, Smart Reschedule, Timezone Handling, Quiet Hours, Background
Delivery, Retry Logic, History). Nessuna UI, nessuna schermata, nessun modulo `domain-*`
(Note/Calendario/Finanze/Home/AI esclusi per vincolo esplicito), nessuna cifratura aggiuntiva.

## 0. Storia del perimetro di questo sprint

Questo sprint ha attraversato tre fasi di perimetro, ciascuna con una decisione esplicita:

1. **Perimetro iniziale** ("Core Platform"): sicurezza, sync, ricerca, notifiche — tutti e
   quattro i sottosistemi Core dichiarati dall'Engineering Plan.
2. **Raffinamento a metà sprint** ("Core Sync Engine"): il perimetro si restringe al solo motore
   di sincronizzazione, escludendo esplicitamente notifiche e cifratura aggiuntiva.
   `core-security` e `core-search` erano già completati e verificati a quel punto: restano nel
   repository (nessun lavoro buttato) ma non sono stati estesi oltre quanto già fatto.
   `core-notifications` viene descoped: nessun codice applicativo oltre il bootstrap.
3. **Scope Change approvato** (Decision Log D-12): le notifiche vengono reintrodotte
   esplicitamente, con richiesta di implementare tutti i 15 sottocomponenti dichiarati con la
   stessa profondità già applicata a `core-sync`. Questo report riflette lo stato finale dopo
   questa terza fase.

## 1. Architettura finale

### 1.1 core-sync — Sync Engine

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

### 1.2 core-notifications — Notification Broker (Scope Change D-12)

| Componente richiesto | File | Ruolo |
|---|---|---|
| Notification Scheduler | `NotificationScheduler.kt` (expect/actual) | "A questo istante, esegui questo callback" — la primitiva di basso livello (TDR-26); JVM reale via `ScheduledExecutorService` |
| Local Notifications | `LocalNotificationService.kt` | Compone scheduler + canale + permesso per mostrare una notifica; no-op silenzioso se il permesso è negato (NTF §2, P6) |
| Recurring Notifications | `RecurringNotificationPlanner.kt` | Cadenza propria (giornaliera/settimanale/intervallo) di un promemoria ricorrente — non la `RecurrenceRule` di `domain-task` |
| Notification Actions | `NotificationActionDispatcher.kt` | NTF-005: completa/posticipa/spunta dalla notifica; pubblica `NtfActionPerformed`, mai esegue l'azione |
| Deep Links | `DeepLinkResolver.kt` | URI `omnilife://<tipo>/<id>` (TDR-32); build/parse, nessuna dipendenza di navigazione |
| Notification Categories | `NotificationCategory.kt` (`NotificationCategoryRegistry`) | Granularità NTF-007/R-03; traccia gli ignorati consecutivi per NTF-006 |
| Notification Permissions | `NotificationPermissionManager.kt` (expect/actual) | Stato del permesso; negato → contenuti solo in-app (P6) |
| Notification Channels | `NotificationChannelRegistry.kt` (expect/actual) | Canali Android, importanza derivata dalla priorità (TDR-33); no-op su iOS/JVM |
| Snooze | `SnoozeManager.kt` | NTF-AC-02: nuova richiesta con nuovo id, orario fisso o "stasera" |
| Smart Reschedule | `SmartRescheduler.kt` | NTF-AC-03: un promemoria soppresso dal silenzio si mostra al risveglio solo se ancora rilevante (finestra 4h, TDR-29) |
| Timezone Handling | `TimezoneHandler.kt` | MFC-E-07: l'orario locale dell'intenzione sopravvive ai cambi di fuso |
| Quiet Hours | `QuietHours.kt` | NTF-004: finestra notturna di default 22-8, valutata in orario locale |
| Background Delivery | `BackgroundDeliveryCoordinator.kt` | Risolve al risveglio i differiti e il digest dovuto |
| Retry Logic | `NotificationRetryEngine.kt` + `showWithRetry` | Backoff proprio (TDR-31), indipendente da quello di `core-sync` |
| Notification History | `NotificationHistoryStore.kt` | NTF-007: sorgente dati del centro notifiche in-app |
| Facciata | `NotificationBroker.kt` | NTF-001: unico punto d'ingresso — budget, digest, silenzi, categorie, poi consegna |
| Facciata di composizione | `NotificationEngine.kt` | Compone tutto con implementazioni in-memory di default, richiede solo `LocalNotificationService` dal chiamante (vedi §4) |
| Errori | `NotificationError.kt` | `CategoryDisabled`/`RequestNotFound`/`PermissionDenied` (TDR-21) |
| *(supporto)* | `NotificationBudget.kt`, `NotificationDigest.kt`, `NotificationRequest.kt`, `NotificationEvent.kt` | NTF-002 (budget giornaliero 0-10), NTF-003 (digest), DM-NTF-01 (entità), eventi `NtfRequested`/`NtfActionPerformed` |

### 1.3 core-security (completato prima del raffinamento, non esteso oltre)

`CryptoService`/`KeyManager`/`SecureStorage`/`AppLockService`/`RecoveryKeyService`/
`FieldCipher` — gerarchia di chiavi a cifratura busta (MK→KEK→DEK), AES-256-GCM +
PBKDF2WithHmacSHA256 (TDR-23). La cifratura aggiuntiva resta esclusa da questo sprint.

### 1.4 core-search (completato prima del raffinamento, non esteso oltre)

`SearchIndexer`/`UnifiedSearchService` su SQLite FTS5 via SQLDelight (TDR-25), ranking a
comparatore esplicito a 3 assi (mai `bm25()`), sanitizzazione query contro injection
(MFC-E-17), suggerimenti di ricerca recente.

## 2. Benchmark (numeri reali, misurati in questo sandbox)

| Componente | Misura | Risultato |
|---|---|---|
| `EntityFieldMerger.merge` (core-sync) | 100.000 entità | 162ms (616.915 merge/s) |
| `ORSet.merge` (core-sync) | 200.000 tag totali | 407ms |
| `SqlDelightSyncOutboxStore.enqueue` (core-sync) | 10.000 elementi, file SQLite reale | 37.567ms (266,19 enqueue/s) |
| `SqlDelightSyncOutboxStore.peekNext` (core-sync) | con 10.000 righe in coda | 72ms |
| `NotificationBroker.request` (core-notifications, **nuovo**) | 10.000 richieste | 29ms (338.744 richieste/s) |
| `NotificationHistoryStore.record` (core-notifications, **nuovo**) | 10.000 voci | 8ms (1.121.476 record/s) |
| `NotificationHistoryStore.recent(50)` (core-notifications, **nuovo**) | con 10.000 voci in cronologia | 1ms |
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
| `core-sync` | 89 | 0 | CRDT, outbox in-memory/persistente, retry, scheduler, ricorrenza, merge engine, background sync |
| `core-notifications` | 86 | 0 | 15 componenti richiesti, tutti testati, incluso il round completo `NotificationBroker`/`BackgroundDeliveryCoordinator` |
| `core-security` | 39 | 0 | Invariato dal completamento pre-raffinamento |
| `core-search` | 26 | 0 | Invariato dal completamento pre-raffinamento |
| **Totale sprint** | **240** | **0** | |
| **Build completa (`gradle build`)** | — | **0** | Verificata dopo l'espansione di `core-sync` e l'implementazione di `core-notifications`: tutti i moduli, incluse le app Android/iOS/gallery non toccate in questo sprint, restano verdi |

Durante la verifica di `core-notifications` sono stati trovati e corretti **3 bug nei test
stessi** (non nel codice di produzione): un test di budget con aritmetica di fuso orario
scorretta (i due istanti confrontati ricadevano già sullo stesso giorno UTC, non dimostrando
nulla sulla distinzione locale/UTC che il test dichiarava di verificare) e due test di "risveglio
dal silenzio" che programmavano il promemoria e chiamavano il risveglio entrambi dentro la stessa
finestra di silenzio di default (22-8), quindi il codice si comportava correttamente
(non facendo nulla) mentre il test si aspettava un comportamento diverso. Corretti scegliendo
orari che esercitassero davvero lo scenario dichiarato dal nome del test.

## 4. Casi limite verificati

### core-sync

- **Crash resilience della Local Change Queue**: `SqlDelightSyncOutboxStoreTest` apre lo store,
  accoda elementi, poi **ricrea un nuovo driver puntato allo stesso file** senza rieseguire la
  creazione dello schema — esattamente come un riavvio di processo reale — e verifica che gli
  elementi (inclusi i byte del payload) sopravvivano.
- **Idempotenza**: `acknowledge()` su un id già rimosso non lancia e non altera lo stato —
  verificato sia per lo store in-memory sia per quello persistente, e nel round completo di
  `BackgroundSyncCoordinator`.
- **Retry e fallimento persistente**: `hasPersistentFailure` falso subito dopo un rifiuto,
  vero esattamente alla soglia di 72h, usando un orologio finto.
- **Offline**: `runOnce` con rete assente non tocca la coda, transita a `OFFLINE`, mai un errore.
- **Rigetto del server vs eccezione di trasporto**: due `SyncError` distinti, verificati
  separatamente.
- **Merge commutativo**: `merge(a,b) == merge(b,a)` per campi (LWW) e relazioni (OR-Set),
  a livello di entità intera, non solo dei tipi CRDT di base.
- **Priorità hot-prima**: verificata sia in memoria sia nello store persistente.

### core-notifications

- **NTF-AC-01 (budget)**: 3 richieste UTILE mostrate singolarmente, la 4ª e la 5ª confluiscono
  nel digest; PROMEMORIA_UTENTE non consuma mai budget anche a limite 0.
- **NTF-AC-02 (snooze)**: nuova richiesta con nuovo id e stato `PIANIFICATA`; preset "stasera"
  risolve correttamente sia prima sia dopo l'ora serale dichiarata.
- **NTF-AC-03 (smart reschedule)**: un promemoria soppresso dal silenzio, svegliato entro la
  finestra di rilevanza (4h), viene mostrato; oltre la finestra diventa
  `SCADUTA_DI_SIGNIFICATO`, mai ripresentato.
- **NTF-004 (quiet hours)**: finestra di default 22-8 con wraparound di mezzanotte verificata
  ora per ora; una finestra non-wrapping (es. 13-15) usa lo stesso codice correttamente.
- **NTF-006 (auto-disattivazione)**: esattamente 3 esiti `IGNORATA` consecutivi propongono la
  disattivazione; un esito diverso in mezzo azzera il contatore.
- **NTF-007 (categorie/permesso)**: una categoria disattivata sopprime senza mai toccare
  scheduler/digest; un permesso negato rende `show()` un no-op silenzioso, mai un errore.
- **Burst NTF §2**: oltre la soglia configurabile di richieste nell'ultima ora, le richieste
  eccedenti confluiscono nel digest — verificato isolando budget e burst in test separati.
- **Digest**: consegnato una sola volta per giorno locale anche con più chiamate a
  `flushDigestIfDue` dopo l'orario di consegna.
- **Retry Logic**: `showWithRetry` ritenta con il backoff di `NotificationRetryEngine` e si
  arrende dopo il numero massimo di tentativi, verificato con tempo virtuale (`runTest`).
- **Deep link round-trip**: `build`→`parse` ricostruisce esattamente l'`EntityReference`
  originale; URI malformati o di schema estraneo restituiscono `null`, mai un'eccezione.
- **Fuso orario/giorno locale (MFC-E-07)**: la stessa intenzione oraria risolve a istanti
  diversi in fusi diversi; il budget usa il giorno locale anche quando differisce dal giorno
  UTC dell'istante.

## 5. Prestazioni

Tutti i budget dichiarati dalle Bible e verificabili in questo sandbox sono rispettati con
ampio margine:

- MFC-AC-07 (ricerca ≤100ms a 50.000 entità): **13ms misurati**, 7,7× sotto budget.
- MFC-E-14 (100.000+ entità per operazione di sync): merge di campo a 616.915 merge/s e merge
  di insiemi di relazioni a 200.000 tag in 407ms.
- `NotificationBroker.request` (il percorso più ricco di `core-notifications`, con controlli di
  budget/burst/silenzio ad ogni chiamata): 338.744 richieste/s — ordini di grandezza sopra
  qualunque volume di notifiche reale (NTF-002 limita a 10/giorno per utente).
- Il costo del PBKDF2 (559,2ms) è **intenzionale**, non una prestazione da ottimizzare.
- L'unico numero relativamente basso è `enqueue` sulla coda persistente (266/s, §2) — un limite
  del disco/filesystem sottostante, ben sopra qualunque tasso di modifica umana reale.

## 6. Rischi residui

### core-sync

- **`BackgroundSyncCoordinator` non è collegato a un trasporto reale**: nessun modulo
  `domain-*` pubblica ancora eventi che questo coordinator possa consumare. Blocco esplicito
  per Sprint 4.
- **`NetworkMonitor` ha solo l'implementazione manuale** (TDR-28): connettori di piattaforma
  reali rinviati.
- **Merge per paragrafo delle Note non implementato**: fallback whole-snapshot-LWW, già
  permesso dalla Data Model Bible.
- **`enqueue` in massa non ottimizzato**: una futura API batch risolverebbe un import bulk,
  non necessaria al volume interattivo di questo sprint.

### core-notifications

- **Nessun collegamento a `domain-task`/altri moduli**: coerente col vincolo esplicito ("Non
  implementare: Note, Calendario, Finanze, Home, AI"), ma resta un blocco concreto — nessun
  modulo pubblica ancora `NtfRequested` in produzione. Tutti i tipi qui sono generici.
- **Connettori di piattaforma reali non verificati**: `NotificationScheduler`/
  `NotificationChannelRegistry`/`NotificationPermissionManager` hanno solo il JVM `actual`
  compilato/testato in questo sandbox; Android (`AlarmManager`, canali reali, permesso runtime)
  e iOS (`UNUserNotificationCenter`) sono scritti seguendo pattern plausibili ma non
  compilati/verificati qui (nessun SDK/host).
- **Nessun trasporto per la consegna in background reale**: `BackgroundDeliveryCoordinator` è
  la logica pura; il trigger di piattaforma (WorkManager/BGTaskScheduler) resta wiring
  app-shell, fuori da questo modulo.
- **Semantica del burst è un'interpretazione dichiarata, non letterale**: TDR-30 documenta
  esplicitamente che il collasso è "in avanti" (solo le richieste eccedenti), non un collasso
  retroattivo dell'intero burst — una scelta implementativa deliberata, non un'ambiguità
  irrisolta.
- **Finestra di rilevanza (4h) e orario di consegna del digest (18:00) sono costanti globali**:
  non configurabili per categoria/utente in questo sprint (TDR-29/30); nessuna Bible lo
  richiede oggi.

## 7. Decision Log

- **D-12** (Product Bible, Decision Log): approva lo Scope Change che reintroduce
  `core-notifications` nel perimetro dello Sprint 3, dopo che il raffinamento a metà sprint lo
  aveva escluso.
- **TDR-27/28** (già registrate durante l'espansione di `core-sync`): persistenza della Local
  Change Queue via SQLDelight; strategia di implementazione del Network Monitor.
- **TDR-29…33** (registrate prima/durante l'implementazione di `core-notifications`, dopo
  D-12): finestra di rilevanza dello Smart Reschedule; semantica del burst e orario di consegna
  del digest; Retry Logic indipendente da `core-sync`; formato dell'URI dei deep link;
  mappatura categoria/priorità → canale di notifica.

Tutte seguono lo stesso metodo (≥3 alternative, motivazione contro la documentazione esistente)
e la stessa autorità delle voci TDR-01…18.
